package upec.episen.eco.models.optimization;

import upec.episen.eco.models.consumption.Consumption;

public class OptimizedConsumptionResult {


    private double optimizedCarbonFootprint;
    private double[] optimizedFrequencies;
    private Consumption consumption;
    private double savedCarbon;

    public OptimizedConsumptionResult(double optimizedCarbonFootprint,double[] optimizedFrequencies, Consumption consumption, double savedCarbon) {
        this.optimizedCarbonFootprint=optimizedCarbonFootprint;
        this.optimizedFrequencies = optimizedFrequencies;
        this.consumption = consumption;
        this.savedCarbon = savedCarbon;
    }

    public double[] getOptimizedFrequencies() {
        return optimizedFrequencies;
    }

    public Consumption getConsumption() {
        return consumption;
    }

    public double getSavedCarbon() {
        return this.savedCarbon;
    }

    public double getOptimizedCarbonFootprint() {
        return optimizedCarbonFootprint;
    }
}
