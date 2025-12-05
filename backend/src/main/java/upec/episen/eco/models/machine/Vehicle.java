package upec.episen.eco.models.machine;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import upec.episen.eco.models.machine.enums.UsageCategory;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;

@Entity
public class Vehicle extends Machine {

    @Column(name = "vehicle_size")
    @Enumerated(EnumType.STRING)
    private VehicleSize size;

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    public Vehicle(int id, String name, double f, UsageCategory us, String img, Set<Component> r, VehicleSize size, VehicleType vehicleType) {
        super(id, name, f, us, img, r);
        this.size = size;
        this.vehicleType = vehicleType;
    }

    public Vehicle() {}

    public VehicleSize getSize() {
        return size;
    }

    public void setSize(VehicleSize size) {
        this.size = size;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "size=" + size +
                ", vehicle type=" + vehicleType +
                "} " + super.toString();
    }

    @Override
    public String getType() {
        return "vehicle";
    }
}