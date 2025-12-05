package upec.episen.eco.repositories.norms;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;
import upec.episen.eco.models.norms.VehicleEmissionFactor;

public interface IVehicleEmissionFactor extends JpaRepository<VehicleEmissionFactor, Long>  {

    VehicleEmissionFactor findByEnergyTypeAndTypeAndSize(EnergyType energyType, VehicleType type, VehicleSize size);
}
