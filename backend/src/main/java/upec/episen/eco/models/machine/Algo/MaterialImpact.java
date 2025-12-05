package upec.episen.eco.models.machine.Algo;

public  class MaterialImpact {
    private double emissionFactor;
    private boolean recyclable;

    public MaterialImpact(double emissionFactor, boolean recyclable) {
        this.emissionFactor = emissionFactor;
        this.recyclable = recyclable;
    }

    public double getEmissionFactor() {
        return emissionFactor;
    }

    public boolean isRecyclable() {
        return recyclable;
    }
}