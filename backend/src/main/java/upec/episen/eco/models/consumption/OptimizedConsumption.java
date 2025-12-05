package upec.episen.eco.models.consumption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="optimized_consumption")
public class OptimizedConsumption {
    @Id
    private long id;
    @Column
    private double optimisationRate;

    public OptimizedConsumption(long id, double optimisationRate) {
        this.id = id;
        this.optimisationRate = optimisationRate;
    }

    public OptimizedConsumption() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getOptimisationRate() {
        return optimisationRate;
    }

    public void setOptimisationRate(double optimisationRate) {
        this.optimisationRate = optimisationRate;
    }

    @Override
    public String toString() {
        return "OptimizedConsumption{" +
                "id=" + id +
                ", optimisationRate=" + optimisationRate +
                '}';
    }
}
