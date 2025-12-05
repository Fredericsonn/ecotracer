package upec.episen.eco.models.consumption;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import upec.episen.eco.models.User.User;


@Entity
public class Consumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private double totalCarbonEmitted;

    @Column
    private LocalDate createdAt;

    @OneToMany(mappedBy = "consumption", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JsonManagedReference
    private Set<ConsumptionItem> items;

    @ManyToOne
    private User user;

    public Consumption(String name, User user, Set<ConsumptionItem> items) {
        this.name = name;
        this.user = user;
        this.items = items;
    }

    public Consumption() {}

    public Set<ConsumptionItem> getConsumptionItems() {
        return items;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setConsumptionItems(Set<ConsumptionItem> c){
        this.items=c;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalCarbonEmitted() {
        return totalCarbonEmitted;
    }

    public void setTotalCarbonEmitted(double totalCarbonEmitted) {
        this.totalCarbonEmitted = totalCarbonEmitted;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Consumption [id=" + id + ", name=" + name + ", totalCarbonEmitted=" + totalCarbonEmitted
                + ", createdAt=" + createdAt + ", items=" + items + ", user=" + user + "]";
    }
}
