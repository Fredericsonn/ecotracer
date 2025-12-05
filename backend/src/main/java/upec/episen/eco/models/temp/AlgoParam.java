package upec.episen.eco.models.temp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/* THIS IS A TEMPORARY ENTITY JUST FOR A QUICK R2 DEMO THAT WAS REQUESTED BY MR BRENNER */
@Entity
@Table(name="algo_param")
public class AlgoParam {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column
    private String type;

    @Column(name="energy_input")
    private Double energyInput;

    @Column(name="usage_frequency")
    private double usageFequency;

    @Column(name="emission_factor")
    private double emissionFactor;

    public AlgoParam(String type, double energyInput, double usageFequency, double emissionFactor) {
        this.type = type;
        this.energyInput = energyInput;
        this.usageFequency = usageFequency;
        this.emissionFactor = emissionFactor;
    }

    public AlgoParam() {
        
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getEnergyInput() {
        return energyInput;
    }

    public void setEnergyInput(double energyInput) {
        this.energyInput = energyInput;
    }

    public double getUsageFequency() {
        return usageFequency;
    }

    public void setUsageFequency(double usageFequency) {
        this.usageFequency = usageFequency;
    }

    public double getEmissionFactor() {
        return emissionFactor;
    }

    public void setEmissionFactor(double emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    @Override
    public String toString() {
        return "AlgoParams [id=" + id + ", type=" + type + ", energyInput=" + energyInput + ", usageFequency="
                + usageFequency + ", emissionFactor=" + emissionFactor + "]";
    }

    
    
}
