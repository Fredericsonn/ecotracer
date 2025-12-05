package upec.episen.eco.repositories.norms;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.norms.DeviceEmissionFactor;

public interface IDeviceEmissionFactor extends JpaRepository<DeviceEmissionFactor, Long> {

    DeviceEmissionFactor findByEnergyType(EnergyType type);
}
