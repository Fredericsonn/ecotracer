package upec.episen.eco.service.OptimizedConsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.consumption.advancedOptimization.*;
import upec.episen.eco.service.consumption.ConsumptionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class AdvancedOptimizationService {

    @Autowired
    private ConsumptionService consumptionService;

    private static final double COST_PER_KWH = 0.2016;
    private static final int MAX_ITERATIONS = 100;
    private static final double CARBON_TOLERANCE = 0.1;
    private static final double ADJUSTMENT_STEP = 0.05;

    public AdvancedOptimizationResult findOptimalUsageFrequencies(AdvancedOptimizationRequest request) {

        try {
            Consumption consumption = consumptionService.getConsumptionById(request.getConsumptionId());
            if (consumption == null || consumption.getConsumptionItems() == null || consumption.getConsumptionItems().isEmpty()) {
                return new AdvancedOptimizationResult(false, "Consumption not found or has no items.");
            }
            List<ConsumptionItem> items = new ArrayList<>(consumption.getConsumptionItems());

            if (request.getCarbonReductionPercentageCi() < 0 || request.getCarbonReductionPercentageCi() > 1) {
                return new AdvancedOptimizationResult(false, "Carbon reduction percentage (Ci) must be between 0 and 1.");
            }
            if (request.getBudgetT() < 0) {
                return new AdvancedOptimizationResult(false, "Budget (T) cannot be negative.");
            }

            Map<Long, FrequencyConstraint> constraintsMap = validateAndMapConstraints(request.getFrequencyConstraints(), items);
            if (constraintsMap == null) {
                return new AdvancedOptimizationResult(false, "Invalid frequency constraints provided (mismatch with items, or min > max).");
            }

            double originalTotalCarbon = consumption.getTotalCarbonEmitted();
            double targetTotalCarbon = originalTotalCarbon * (1.0 - request.getCarbonReductionPercentageCi());
            double maxEnergyKWh = (COST_PER_KWH > 0) ? request.getBudgetT() / COST_PER_KWH : Double.POSITIVE_INFINITY;

            double[] currentFrequencies = initializeFrequencies(items, constraintsMap);
            double[] originalFrequencies = items.stream().mapToDouble(ConsumptionItem::getUsageFrequency).toArray();

            boolean solutionFound = false;
            double achievedTotalCarbon = -1;
            double achievedTotalEnergy = -1;

            for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
                achievedTotalCarbon = calculateTotalCarbon(items, currentFrequencies);
                achievedTotalEnergy = calculateTotalEnergyKWh(items, currentFrequencies);

                double carbonDelta = achievedTotalCarbon - targetTotalCarbon;

                if (Math.abs(carbonDelta) <= CARBON_TOLERANCE && achievedTotalEnergy <= maxEnergyKWh) {
                    solutionFound = true;
                    break;
                }

                if (achievedTotalEnergy > maxEnergyKWh * 1.01) {
                }

                if (!adjustFrequencies(items, currentFrequencies, constraintsMap, carbonDelta, targetTotalCarbon)) {
                    break;
                }
            }

            AdvancedOptimizationResult result;
            if (solutionFound) {
                result = new AdvancedOptimizationResult(true, "Optimal solution found within constraints.");
                result.setAchievedTotalCarbon(achievedTotalCarbon);
                result.setAchievedTotalCost(achievedTotalEnergy * COST_PER_KWH);

                List<OptimizedItemResult> optimizedItemsList = IntStream.range(0, items.size())
                        .mapToObj(i -> new OptimizedItemResult(
                                items.get(i).getId(),
                                items.get(i).getName(),
                                originalFrequencies[i],
                                currentFrequencies[i],
                                items.get(i).getEnergyInput(),
                                calculateIndividualItemCarbon(items.get(i), currentFrequencies[i])
                        ))
                        .collect(Collectors.toList());
                result.setOptimizedItems(optimizedItemsList);

            } else {
                result = new AdvancedOptimizationResult(false, "Could not find a feasible solution within the given constraints and iterations.");
                result.setAchievedTotalCarbon(calculateTotalCarbon(items, currentFrequencies));
                result.setAchievedTotalCost(calculateTotalEnergyKWh(items, currentFrequencies) * COST_PER_KWH);
            }

            result.setOriginalTotalCarbon(originalTotalCarbon);
            result.setTargetTotalCarbon(targetTotalCarbon);
            result.setOriginalTotalCost(calculateTotalEnergyKWh(items, originalFrequencies) * COST_PER_KWH);
            result.setTargetMaxCost(request.getBudgetT());

            return result;


        } catch (Exception e) {
            return new AdvancedOptimizationResult(false, "An unexpected error occurred during optimization: " + e.getMessage());
        }
    }

    private Map<Long, FrequencyConstraint> validateAndMapConstraints(List<FrequencyConstraint> constraints, List<ConsumptionItem> items) {
        if (constraints == null || constraints.size() != items.size()) {
            return null;
        }
        Map<Long, FrequencyConstraint> map = new HashMap<>();
        Set<Long> itemIds = items.stream().map(ConsumptionItem::getId).collect(Collectors.toSet());

        for (FrequencyConstraint c : constraints) {
            if (!itemIds.contains(c.getConsumptionItemId()) || c.getMinFrequency() < 0 || c.getMaxFrequency() < c.getMinFrequency()) {
                return null;
            }
            map.put(c.getConsumptionItemId(), c);
        }
        if (map.size() != items.size()){
            return null;
        }
        return map;
    }

    private double[] initializeFrequencies(List<ConsumptionItem> items, Map<Long, FrequencyConstraint> constraintsMap) {
        double[] initialFrequencies = new double[items.size()];
        for (int i = 0; i < items.size(); i++) {
            ConsumptionItem item = items.get(i);
            FrequencyConstraint constraint = constraintsMap.get(item.getId());
            double midPoint = (constraint.getMinFrequency() + constraint.getMaxFrequency()) / 2.0;
            initialFrequencies[i] = Math.max(constraint.getMinFrequency(), Math.min(midPoint, constraint.getMaxFrequency()));
        }
        return initialFrequencies;
    }


    private double calculateTotalCarbon(List<ConsumptionItem> items, double[] frequencies) {
        double totalCarbon = 0;
        for (int i = 0; i < items.size(); i++) {
            totalCarbon += calculateIndividualItemCarbon(items.get(i), frequencies[i]);
        }
        return totalCarbon;
    }

    private double calculateIndividualItemCarbon(ConsumptionItem item, double frequency) {
        ConsumptionItem tempItem = new ConsumptionItem();
        tempItem.setMachine(item.getMachine());
        tempItem.setEnergyInput(item.getEnergyInput());
        tempItem.setQuantity(item.getQuantity());
        tempItem.setEnergyType(item.getEnergyType());
        tempItem.setUsageFrequency(frequency);
        return consumptionService.calculateItemCarbonFootprint(tempItem, item.getEnergyType());
    }


    private double calculateTotalEnergyKWh(List<ConsumptionItem> items, double[] frequencies) {
        double totalEnergy = 0;
        for (int i = 0; i < items.size(); i++) {
            totalEnergy += (frequencies[i] * items.get(i).getEnergyInput()) / 1000.0;
        }
        return totalEnergy;
    }

    private boolean adjustFrequencies(List<ConsumptionItem> items, double[] currentFrequencies, Map<Long, FrequencyConstraint> constraintsMap, double carbonDelta, double targetCarbon) {
        boolean adjustmentMade = false;

        List<ItemSensitivity> sensitivities = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            double currentCarbon = calculateIndividualItemCarbon(items.get(i), currentFrequencies[i]);
            double slightlyHigherFreq = currentFrequencies[i] + 0.1;
            double carbonAtHigherFreq = calculateIndividualItemCarbon(items.get(i), slightlyHigherFreq);
            double sensitivity = (carbonAtHigherFreq - currentCarbon) / 0.1;
            sensitivities.add(new ItemSensitivity(i, sensitivity));
        }


        if (carbonDelta > CARBON_TOLERANCE) {
            sensitivities.sort(Comparator.comparingDouble(ItemSensitivity::getSensitivity).reversed());

            for (ItemSensitivity is : sensitivities) {
                int i = is.getIndex();
                FrequencyConstraint constraint = constraintsMap.get(items.get(i).getId());
                double currentFreq = currentFrequencies[i];

                if (currentFreq > constraint.getMinFrequency()) {
                    double reductionAmount = Math.abs(carbonDelta / (is.getSensitivity() != 0 ? is.getSensitivity() : 1E-9) * ADJUSTMENT_STEP) ; // Avoid division by zero
                    currentFrequencies[i] = Math.max(constraint.getMinFrequency(), currentFreq - reductionAmount * currentFreq);

                    if (currentFrequencies[i] < currentFreq) {
                        adjustmentMade = true;
                    }
                }
            }

        } else if (carbonDelta < -CARBON_TOLERANCE) {
            sensitivities.sort(Comparator.comparingDouble(ItemSensitivity::getSensitivity));

            for (ItemSensitivity is : sensitivities) {
                int i = is.getIndex();
                FrequencyConstraint constraint = constraintsMap.get(items.get(i).getId());
                double currentFreq = currentFrequencies[i];

                if (currentFreq < constraint.getMaxFrequency()) {
                    double increaseAmount = Math.abs(carbonDelta / (is.getSensitivity() != 0 ? is.getSensitivity() : 1E-9) * ADJUSTMENT_STEP) ; // Avoid division by zero
                    currentFrequencies[i] = Math.min(constraint.getMaxFrequency(), currentFreq + increaseAmount * currentFreq);

                    if (currentFrequencies[i] > currentFreq) {
                        adjustmentMade = true;
                    }
                }
            }
        } else {
            adjustmentMade = true;
        }

        return adjustmentMade;
    }

    private static class ItemSensitivity {
        private final int index;
        private final double sensitivity;

        ItemSensitivity(int index, double sensitivity) {
            this.index = index;
            this.sensitivity = sensitivity;
        }

        public int getIndex() { return index; }
        public double getSensitivity() { return sensitivity; }
    }

}