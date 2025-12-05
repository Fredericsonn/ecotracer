package upec.episen.eco.controllers.optimization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upec.episen.eco.exceptions.ConsumptionNotFoundException;
import upec.episen.eco.service.OptimizedConsumption.OptimizationService;
import upec.episen.eco.models.optimization.OptimizedConsumptionResult;
import upec.episen.eco.service.OptimizedConsumption.QuizService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/minimal")
public class OptimizationController {

    @Autowired
    private OptimizationService optimizationService;

    @Autowired
    private QuizService quizService;


    @PostMapping("/constraints/{consumptionId}")
    public ResponseEntity<?> saveConstraints(@PathVariable Long consumptionId, @RequestBody Map<Long, Double> constraints) {
        try {
            quizService.saveConstraints(consumptionId, constraints);
            return ResponseEntity.ok("Constraints saved.");
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving constraints: " + e.getMessage());
        }
    }



    @PostMapping("/optimize/{consumptionId}")
    public ResponseEntity<?> optimize(@PathVariable Long consumptionId) {
        try {
            OptimizedConsumptionResult result = optimizationService.optimizeConsumption(consumptionId);

            Map<String, Object> response = new HashMap<>();
            response.put("optimizedFrequencies", result.getOptimizedFrequencies());
            response.put("savedCarbon", result.getSavedCarbon());
            response.put("optimizedCarbonFootprint", result.getOptimizedCarbonFootprint()); // Add this line

            return ResponseEntity.ok(response);
        } catch (ConsumptionNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Optimization error: " + e.getMessage());
        }
    }
}