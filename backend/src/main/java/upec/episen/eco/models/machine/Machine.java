package upec.episen.eco.models.machine;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import upec.episen.eco.models.User.Collection;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.machine.enums.UsageCategory;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Device.class, name = "Device"),
    @JsonSubTypes.Type(value = Vehicle.class, name = "Vehicle")
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column(name = "default_footprint")
    private double defaultFootprint;

    @Column
    @Enumerated(EnumType.STRING)
    private UsageCategory usage;

    @Column
    private String img;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Component> resources = new HashSet<>();

    @ManyToMany(mappedBy="machines")
    private Set<Collection> collection;

    @OneToMany(mappedBy = "machine")
    private Set<ConsumptionItem> items;

    public Machine(int id, String name, double f, UsageCategory us, String img, Set<Component> r) {
        this.id = id;
        this.name = name;
        this.defaultFootprint = f;
        this.usage = us;
        this.img = img;
        this.resources = r;
    }

    public Machine() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDefaultFootprint() {
        return defaultFootprint;
    }

    public void setDefaultFootprint(double defaultFootprint) {
        this.defaultFootprint = defaultFootprint;
    }

    public UsageCategory getUsage() {
        return usage;
    }

    public void setUsage(UsageCategory usage) {
        this.usage = usage;
    }

    public Set<Component> getResources() {
        return resources;
    }

    public void setResources(Set<Component> resources) {
        this.resources = resources;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", defaultFootpring=" + defaultFootprint +
                ", usage=" + usage +
                ", img=" + img +
                ", resources=" + resources +
                '}';
    }

}
