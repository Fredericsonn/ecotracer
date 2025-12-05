package upec.episen.eco.controllers.consumption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import upec.episen.eco.exceptions.ConsumptionNotFoundException;
import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.service.consumption.ConsumptionItemService;
import upec.episen.eco.service.consumption.ConsumptionService;

@RestController
@RequestMapping("/consumptions")
public class ConsumptionController {

    @Autowired
    private ConsumptionService consumptionservice;

    @Autowired
    private ConsumptionItemService itemservice;

    @GetMapping
    public ResponseEntity<?> getAllConsumption(@RequestParam(name = "userId") Long userId) {
        Map<String, Object> body = new HashMap<String, Object>();
        String msg;
        HttpStatus status = HttpStatus.OK;

        if (userId != null) {
            try {
                List<Consumption> consumptions = consumptionservice.getAllConsumptionsByUser(userId);
                body.put("consumptions", consumptions);
            } catch (UserNotFoundException e) {
                msg = e.getMessage();
                body.put("msg", msg);
                status = HttpStatus.NOT_ACCEPTABLE;
            }

            return ResponseEntity.status(status).body(body);
        }

        return ResponseEntity.status(status).body(consumptionservice.getAllConsumptions());

    }

    @GetMapping("/{id}")
    public Consumption getConsumptionById(@PathVariable long id) {

        try {
            return consumptionservice.getConsumptionById(id);
        } catch (ConsumptionNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/{id}/items")
    public List<ConsumptionItem> getOrderedItems(@PathVariable Long id){
        return consumptionservice.getOrderedItemsById(id);
    }

    @PostMapping("/post")
    public ResponseEntity<?> postConsumption(@RequestBody Consumption consumption) {
        String msg;
        HttpStatus status;
        Map<String, Object> body = new HashMap<String, Object>();

        try {
            Consumption c = consumptionservice.saveConsumption(consumption);
            msg = "consumption created successfully";
            status = HttpStatus.OK;
            body.put("consumption", c);
        } catch (Exception e) {
            msg = e.getMessage();
            status = HttpStatus.NOT_ACCEPTABLE;
        }

        body.put("msg", msg);
        
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping("/post/temp")
    public Consumption postConsumptionTemp(@RequestBody Consumption consumption) {

        return consumptionservice.saveConsumptionTemp(consumption);
    }

    @PostMapping("/items/post")
    public ResponseEntity<?> postConsumptionItems(@RequestBody List<ConsumptionItem> items) {
        String msg;
        HttpStatus status;

        try {
            itemservice.saveConsumptionItem(items);
            msg = "consumption items created successfully";
            status = HttpStatus.OK;
        } catch (Exception e) {
            msg = e.getMessage();
            status = HttpStatus.NOT_ACCEPTABLE;
        }

        return ResponseEntity.status(status).body(Map.of("msg", msg));
    }

    @PostMapping("/mir")
    public ResponseEntity<?> getManufacturingImpactRatioReport(@RequestBody Consumption consumption) {

        Map<String, Object> body = new HashMap<>();

        double consumptionDefaultFootprint= consumptionservice.calculateConsumptionImpactScore(consumption);

        Map<Long, Double> MIR_Report = consumptionservice.itemsMIReporter(consumption);

        body.put("score", consumptionDefaultFootprint);
        body.put("report", MIR_Report);

        return ResponseEntity.ok(body);
    }
}
