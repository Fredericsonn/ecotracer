package upec.episen.eco.models.machine;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Matter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String value;

    @Column
    private double volume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Component component;

    public Matter() {}

    public Matter(String value, double volume) {
        this.value = value;
        this.volume = volume;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }


    @Override
    public String toString() {
        return "Matter{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", volume=" + volume +
                '}';
    }
}
