package upec.episen.eco.models.machine.Algo;

import java.util.List;

public class MachineDetailDto {
    private int id;
    private String name;
    private String machineType;
    private String usageCategory;
    private double defaultFootprint;
    private double footprint;
    private double recyclability;
    private String vehicleSize;
    private String vehicleType;
    private List<ComponentDto> components;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMachineType() { return machineType; }
    public void setMachineType(String machineType) { this.machineType = machineType; }

    public String getUsageCategory() { return usageCategory; }
    public void setUsageCategory(String usageCategory) { this.usageCategory = usageCategory; }

    public double getDefaultFootprint() { return defaultFootprint; }
    public void setDefaultFootprint(double defaultFootprint) { this.defaultFootprint = defaultFootprint; }

    public double getFootprint() { return footprint; }
    public void setFootprint(double footprint) { this.footprint = footprint; }

    public double getRecyclability() { return recyclability; }
    public void setRecyclability(double recyclability) { this.recyclability = recyclability; }

    public String getVehicleSize() { return vehicleSize; }
    public void setVehicleSize(String vehicleSize) { this.vehicleSize = vehicleSize; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public List<ComponentDto> getComponents() { return components; }
    public void setComponents(List<ComponentDto> components) { this.components = components; }
}

