package upec.episen.eco.service.consumption;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upec.episen.eco.exceptions.ConsumptionNotFoundException;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.repositories.consumption.IConsumptionItem;

@Service
public class ConsumptionItemService {

    @Autowired
    private IConsumptionItem itemRepository;

    @Autowired
    private ConsumptionService consumptionService;

    public List<ConsumptionItem> getConsumptionItemsByConsumption(Long consumptionId) throws ConsumptionNotFoundException {
        Consumption consumption = consumptionService.getConsumptionById(consumptionId);
        return itemRepository.findByConsumption(consumption);
    }

    public ConsumptionItem saveConsumptionItem(ConsumptionItem item) {
        return itemRepository.save(item);
    }

    public void saveConsumptionItem(List<ConsumptionItem> items) {
        items.forEach(item -> itemRepository.save(item));
    }
}
