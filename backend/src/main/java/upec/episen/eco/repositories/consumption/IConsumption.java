package upec.episen.eco.repositories.consumption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import upec.episen.eco.models.User.User;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;

@Repository
public interface IConsumption extends JpaRepository<Consumption,Long> {

    @Query("SELECT ci FROM consumption_item ci WHERE ci.consumption.id = :consumptionId " + "ORDER BY ci.carbonFootprint / ci.quantity DESC")
    List<ConsumptionItem> getOrderedItemsById(Long consumptionId);

    List<Consumption> findAllByUser(User user);
}
