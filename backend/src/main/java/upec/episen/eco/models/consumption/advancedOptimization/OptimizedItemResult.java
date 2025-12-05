package upec.episen.eco.models.consumption.advancedOptimization;

public class OptimizedItemResult {
    private long consumptionItemId;
    private String itemName;
    private double originalFrequency;
    private double optimizedFrequency;
    private double energyInput;
    private double resultingCarbonFootprint;

    public OptimizedItemResult(long consumptionItemId, String itemName, double originalFrequency, double optimizedFrequency, double energyInput, double resultingCarbonFootprint) {
        this.consumptionItemId = consumptionItemId;
        this.itemName = itemName;
        this.originalFrequency = originalFrequency;
        this.optimizedFrequency = optimizedFrequency;
        this.energyInput = energyInput;
        this.resultingCarbonFootprint = resultingCarbonFootprint;
    }

    public long getConsumptionItemId() { return consumptionItemId; }
    public String getItemName() { return itemName; }
    public double getOriginalFrequency() { return originalFrequency; }
    public double getOptimizedFrequency() { return optimizedFrequency; }
    public double getEnergyInput() { return energyInput; }
    public double getResultingCarbonFootprint() { return resultingCarbonFootprint; }

}