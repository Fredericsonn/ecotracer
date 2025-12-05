package upec.episen.eco.models.consumption;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.Machine;

@Entity(name="consumption_item")
public class ConsumptionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column(name = "energy_input")
    private double energyInput;
    
    @Column
    private double usageFrequency;

    @Column
    private long quantity;
    @Column
    private double carbonFootprint;

    @ManyToOne
    private Machine machine;

    @Column(name = "energy_type")
    @Enumerated(EnumType.STRING)
    private EnergyType energyType;

    @ManyToOne
    @JoinColumn(name = "consumption_id", nullable = false)
    @JsonBackReference
    private Consumption consumption;

    public ConsumptionItem(String name, Machine machine, double energyInput, double usageFrequency, long quantity, EnergyType energyType) {
        this.name = name;
        this.energyInput = energyInput;
        this.usageFrequency = usageFrequency;
        this.quantity = quantity;
        this.energyType = energyType;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public void setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
    }

    public ConsumptionItem() {}

    
    public Consumption getConsumption() {
        return consumption;
    }

    public void setConsumption(Consumption consumption) {
        this.consumption = consumption;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getEnergyInput() {
        return energyInput;
    }

    public void setEnergyInput(double energyInput) {
        this.energyInput = energyInput;
    }

    public double getUsageFrequency() {
        return usageFrequency;
    }

    public void setUsageFrequency(double usageFrequency) {
        this.usageFrequency = usageFrequency;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(double carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "ConsumptionItem [id=" + id + ", name=" + name + ", energyInput=" + energyInput + ", usageFrequency="
                + usageFrequency + ", quantity=" + quantity + ", carbonFootprint=" + carbonFootprint + ", machine="
                + machine + ", energyType=" + energyType + "]";
    }


    
}
