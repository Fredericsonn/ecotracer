package upec.episen.eco.models.norms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;

@Entity
@Table(name="vehicle_emission_factor")
public class VehicleEmissionFactor extends EmissionFactor {

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleSize size;

    public VehicleEmissionFactor(EnergyType energyType, double emissionFactor) {
        super(energyType, emissionFactor);
    }

    public VehicleEmissionFactor() {
    }


    
}
