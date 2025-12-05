package upec.episen.eco.controllers.optimization;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import upec.episen.eco.models.consumption.advancedOptimization.AdvancedOptimizationRequest;
import upec.episen.eco.models.consumption.advancedOptimization.AdvancedOptimizationResult;
import upec.episen.eco.service.OptimizedConsumption.AdvancedOptimizationService;

@RestController
@RequestMapping("/optimize")
public class AdvancedOptimizationController {

    @Autowired
    private AdvancedOptimizationService advancedOptimizationService;

    @PostMapping("/advanced")
    public ResponseEntity<AdvancedOptimizationResult> optimizeConsumptionAdvanced(
            @RequestBody AdvancedOptimizationRequest request) {

        AdvancedOptimizationResult result = advancedOptimizationService.findOptimalUsageFrequencies(request);
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(result);
    }
}