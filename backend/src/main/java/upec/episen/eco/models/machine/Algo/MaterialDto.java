package upec.episen.eco.models.machine.Algo;

public class MaterialDto {
    private String name;
    private double volume;
    private double emissionFactor;
    private boolean recyclable;
    private double footprint;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }

    public double getEmissionFactor() { return emissionFactor; }
    public void setEmissionFactor(double emissionFactor) { this.emissionFactor = emissionFactor; }

    public boolean isRecyclable() { return recyclable; }
    public void setRecyclable(boolean recyclable) { this.recyclable = recyclable; }

    public double getFootprint() { return footprint; }
    public void setFootprint(double footprint) { this.footprint = footprint; }
}
