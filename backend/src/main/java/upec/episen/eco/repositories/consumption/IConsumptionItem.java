package upec.episen.eco.repositories.consumption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;

public interface IConsumptionItem extends JpaRepository<ConsumptionItem, Long> {
    
    List<ConsumptionItem> findByConsumption(Consumption consumption);
    
}

