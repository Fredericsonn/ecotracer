package upec.episen.eco.service.consumption;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upec.episen.eco.exceptions.ConsumptionNotFoundException;
import upec.episen.eco.exceptions.MachineNotFoundException;
import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.models.User.User;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.Algo.MatterImpactScore;
import upec.episen.eco.models.machine.Device;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.models.machine.Vehicle;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;
import upec.episen.eco.models.temp.AlgoParam;
import upec.episen.eco.repositories.consumption.IConsumption;
import upec.episen.eco.repositories.temp.IAlgoParam;
import upec.episen.eco.service.User.UserService;
import upec.episen.eco.service.machine.MachineService;
import upec.episen.eco.service.norms.EmissionFactorService;

@Service
public class ConsumptionService {

    @Autowired
    private IConsumption iconsumption;

    @Autowired
    private UserService userservice;

    @Autowired
    private EmissionFactorService emissionFactorService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private IAlgoParam ialgoparam;

    public List<Consumption> getAllConsumptions() {
        return iconsumption.findAll();
    }

    public List<Consumption> getAllConsumptionsByUser(Long userId) throws UserNotFoundException {
        User user = userservice.getUserById(userId);
        return iconsumption.findAllByUser(user);
    }

    // Dans ConsumptionService.java
    public List<ConsumptionItem> getConsumptionItemsByConsumption(Long consumptionId)
            throws ConsumptionNotFoundException {
        Consumption consumption = getConsumptionById(consumptionId);
        return new ArrayList<>(consumption.getConsumptionItems());
    }

    public Consumption getConsumptionById(Long id) throws ConsumptionNotFoundException {
        Optional<Consumption> consumption = iconsumption.findById(id);

        if (consumption.isPresent())
            return consumption.get();

        throw new ConsumptionNotFoundException(id);
    }

    public List<ConsumptionItem> getOrderedItemsById(Long id) {
        return iconsumption.getOrderedItemsById(id);
    }

    public Consumption saveConsumption(Consumption consumption) {
        consumption.getConsumptionItems().forEach(item -> {
            long id = item.getMachine().getId();
            String type = item.getMachine().getType();
            try {
                Machine machine = machineService.findById(id, type);
                item.setMachine(machine);
            } catch (MachineNotFoundException e) {
                e.printStackTrace();
                return;
            }
        });

        // we calculate each consumption item's emitted carbon
        consumption.getConsumptionItems()
                .forEach(item -> item.setCarbonFootprint(calculateItemCarbonFootprint(item, item.getEnergyType())));

        // we sum each item's emitted carbon and set it as the consumption's total
        // emission
        consumption.setTotalCarbonEmitted(calculateTotalCarbonEmitted(consumption));

        consumption.setCreatedAt(LocalDate.now());

        return iconsumption.save(consumption);
    }

    public Consumption saveConsumptionTemp(Consumption consumption) {
        consumption.getConsumptionItems().forEach(item -> {
            long id = item.getMachine().getId();
            String type = item.getMachine().getType();
            try {
                Machine machine = machineService.findById(id, type);
                item.setMachine(machine);
            } catch (MachineNotFoundException e) {
                e.printStackTrace();
                return;
            }
        });

        // we calculate each consumption item's emitted carbon
        consumption.getConsumptionItems()
                .forEach(item -> {
                    item.setCarbonFootprint(calculateTotalCarbonEmittedTemp(item));
                    item.setQuantity(1);
                });

        consumption.setTotalCarbonEmitted(calculateTotalCarbonEmitted(consumption));

        consumption.setCreatedAt(LocalDate.now());

        return iconsumption.save(consumption);
    }

    private double calculateTotalCarbonEmitted(Consumption consumption) {
        return consumption.getConsumptionItems().stream().mapToDouble(item -> item.getCarbonFootprint()).sum();
    }

    public double calculateItemCarbonFootprint(ConsumptionItem item, EnergyType energyType) {

        Machine machine = item.getMachine(); // we get the item's machine
        double carbonemitted; // the variable that will store the calculated carbon footprint

        /*
         * the method that gets emission factors expects 3 arguments, 2 of them are null
         * in case the item's machine is a device that's
         * 
         * why in this first part we check before proceeding
         */

        // in case the machine is a vehicle we get the corresponding type and size
        VehicleType type = (machine instanceof Vehicle vehicle) ? vehicle.getVehicleType() : null;
        VehicleSize size = (machine instanceof Vehicle vehicle) ? vehicle.getSize() : null;

        // we get the corresponding emission factor
        double emissionFactor = emissionFactorService.getEmissionFactor(energyType, type, size);

        /*
         * Now in this part we calculate the carbon footprint based on the machine type
         */

        // for devices it's pretty straight forward
        if (machine instanceof Device)
            carbonemitted = item.getEnergyInput() * item.getUsageFrequency() * emissionFactor;
        // in KgCO2 in watss or cubic metters in hours in KgCO2/kwh or cubic metters

        // Now it gets a bit messy, if the machine is a vehicle, there are 2 different
        // ways to calculate the carbon footprint
        else {

            /*
             * Electrical vehicle don't produce any CO2, so what we're mesuring is the
             * carbon emitted to generate the electricity it's running on,
             * and the corresponding value in the emission factors table is actually a watts
             * consumption per km, so we still need to multiply it by
             * the electricity's emission factor
             */
            if (energyType == EnergyType.ELECTRIC) {

                // we get the electricity's carbon emission factor
                double electricityEmissionFactor = emissionFactorService.getElectricityEmissionFactor();

                // just to eliminate confusion
                double consumptionPerKm = emissionFactor;

                carbonemitted = item.getUsageFrequency() * consumptionPerKm * electricityEmissionFactor;
                // in KgCO2 in km in kwh/h in KgCO2/kwh
            }

            // if it's running on diesel or gasoline, then we simply get the corresponding
            // emission factor and calculate the footprint
            else
                carbonemitted = item.getUsageFrequency() * emissionFactor;
            // in KgCO2 in km in KgCO2/km
        }

        // the provided values for ELECTRICITY type are in watts, so we converted it to
        // kwh
        if (energyType == EnergyType.ELECTRICITY)
            carbonemitted /= 1000;

        // we round the calculated value to 2 numbers after the comma and return the
        // value of the footprint
        return BigDecimal.valueOf(carbonemitted).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // in this version, the items parameter are all identical and predifined in the AlgoParam entity
    public double calculateTotalCarbonEmittedTemp(ConsumptionItem item) {

        Machine machine = item.getMachine();
        double carbonEmitted;

        if (machine instanceof Device) {
            AlgoParam param = ialgoparam.findByType("Device");

            double usageFrequency = param.getUsageFequency();

            double energyInput = param.getEnergyInput();

            double emission_factor = param.getEmissionFactor();

            carbonEmitted = usageFrequency * energyInput * emission_factor / 1000;

        }

        else {
            AlgoParam param = ialgoparam.findByType("Vehicle");

            double usageFrequency = param.getUsageFequency();

            double emission_factor = param.getEmissionFactor();

            carbonEmitted = usageFrequency * emission_factor;
        }

        return carbonEmitted;
    }

    // a method that iterates over all the machines in the consumption items within a consumption object and sums their corresponding impact scores
    public double calculateConsumptionImpactScore(Consumption consumption) {

        MatterImpactScore mis = new MatterImpactScore();

        return consumption.getConsumptionItems().stream().mapToDouble(item -> mis.calculateMachineFootprint2(item.getMachine())).sum();
    }

    // a method that creates a MIR report for each item in the consumption
    public Map<Long, Double> itemsMIReporter(Consumption consumption) {

        Map<Long, Double> report = new HashMap<>();

        MatterImpactScore mis = new MatterImpactScore();

        // we calculate each item's mir and add it to the report
        consumption.getConsumptionItems().forEach(item -> report.put(item.getId(), mis.calculateMachineFootprint(item.getMachine())));

        return report;
    }

    // a method that calculates a consumption's MIR 
    public double manufacturingImpactRatioCalculator(Consumption consumption) {

        double manufacturingFootprint = calculateConsumptionImpactScore(consumption);

        double energyConsumptionFootprint = consumption.getTotalCarbonEmitted();

        return manufacturingFootprint / (manufacturingFootprint + energyConsumptionFootprint * 365);
    }
}