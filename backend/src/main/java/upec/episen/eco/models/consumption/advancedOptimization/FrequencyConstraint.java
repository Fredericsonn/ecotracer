package upec.episen.eco.models.consumption.advancedOptimization;

public class FrequencyConstraint {
    private long consumptionItemId;
    private double minFrequency;
    private double maxFrequency;

    public long getConsumptionItemId() { return consumptionItemId; }
    public void setConsumptionItemId(long consumptionItemId) { this.consumptionItemId = consumptionItemId; }
    public double getMinFrequency() { return minFrequency; }
    public void setMinFrequency(double minFrequency) { this.minFrequency = minFrequency; }
    public double getMaxFrequency() { return maxFrequency; }
    public void setMaxFrequency(double maxFrequency) { this.maxFrequency = maxFrequency; }
}