package upec.episen.eco.models.consumption.advancedOptimization;

import java.util.List;

public class AdvancedOptimizationRequest {
    private long consumptionId;
    private List<FrequencyConstraint> frequencyConstraints;
    private double budgetT;
    private double carbonReductionPercentageCi;

    public long getConsumptionId() { return consumptionId; }
    public void setConsumptionId(long consumptionId) { this.consumptionId = consumptionId; }
    public List<FrequencyConstraint> getFrequencyConstraints() { return frequencyConstraints; }
    public void setFrequencyConstraints(List<FrequencyConstraint> frequencyConstraints) { this.frequencyConstraints = frequencyConstraints; }
    public double getBudgetT() { return budgetT; }
    public void setBudgetT(double budgetT) { this.budgetT = budgetT; }
    public double getCarbonReductionPercentageCi() { return carbonReductionPercentageCi; }
    public void setCarbonReductionPercentageCi(double carbonReductionPercentageCi) { this.carbonReductionPercentageCi = carbonReductionPercentageCi; }
}
