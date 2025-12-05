package upec.episen.eco.service.norms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;
import upec.episen.eco.repositories.norms.IDeviceEmissionFactor;
import upec.episen.eco.repositories.norms.IVehicleEmissionFactor;

@Service
public class EmissionFactorService {

    @Autowired
    private IDeviceEmissionFactor idevicefactor;

    @Autowired
    private IVehicleEmissionFactor ivehiclefactor;

    public double getDeviceEmissionFactor(EnergyType type) {
        return idevicefactor.findByEnergyType(type).getEmissionFactor();
    }

    public double getVehicleEmissionFactor(EnergyType energyType, VehicleType type, VehicleSize size) {
        return ivehiclefactor.findByEnergyTypeAndTypeAndSize(energyType, type, size).getEmissionFactor();
    }

    public double getEmissionFactor(EnergyType energyType, VehicleType type, VehicleSize size) {
        if (energyType == EnergyType.ELECTRICITY || energyType == EnergyType.NATURAL_GAS ) {
            return getDeviceEmissionFactor(energyType);
        }

        return getVehicleEmissionFactor(energyType, type, size);
    }

    public double getElectricityEmissionFactor() {
        return getEmissionFactor(EnergyType.ELECTRICITY, null, null);
    }
}
