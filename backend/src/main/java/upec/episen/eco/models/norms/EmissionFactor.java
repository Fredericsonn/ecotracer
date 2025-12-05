package upec.episen.eco.models.norms;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import upec.episen.eco.models.consumption.enums.EnergyType;

@MappedSuperclass
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "energy_type")
    @Enumerated(EnumType.STRING)
    private EnergyType energyType;

    @Column(name = "emission_factor")
    private double emissionFactor;

    public EmissionFactor(EnergyType energyType, double emissionFactor) {
        this.energyType = energyType;
        this.emissionFactor = emissionFactor;
    }

    public EmissionFactor() {
        
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public void setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
    }

    public double getEmissionFactor() {
        return emissionFactor;
    }

    public void setEmissionFactor(double emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    @Override
    public String toString() {
        return "EmissionFactor [id=" + id + ", energyType=" + energyType + ", emissionFactor=" + emissionFactor + "]";
    }

    

}
