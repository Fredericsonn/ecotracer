package upec.episen.eco.service.consumption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.consumption.advancedOptimization.AdvancedOptimizationRequest;
import upec.episen.eco.models.consumption.advancedOptimization.AdvancedOptimizationResult;
import upec.episen.eco.models.consumption.advancedOptimization.FrequencyConstraint;
import upec.episen.eco.service.OptimizedConsumption.AdvancedOptimizationService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvancedOptimizationServiceTest {

    @Mock
    private ConsumptionService consumptionService;

    @InjectMocks
    private AdvancedOptimizationService optimizationService;

    @Test
    void testFindOptimalUsageFrequencies_success() throws Exception {
        ConsumptionItem item1 = new ConsumptionItem();
        item1.setId(1L);
        item1.setName("Machine A");
        item1.setEnergyInput(1000); // en watts
        item1.setUsageFrequency(5.0);
        item1.setQuantity(1);

        Consumption consumption = new Consumption();
        consumption.setId(10L);
        consumption.setConsumptionItems(Set.of(item1));
        consumption.setTotalCarbonEmitted(50.0);

   
        when(consumptionService.getConsumptionById(10L)).thenReturn(consumption);
        when(consumptionService.calculateItemCarbonFootprint(any(), any())).thenReturn(45.0);

        FrequencyConstraint constraint = new FrequencyConstraint();
        constraint.setConsumptionItemId(1L);
        constraint.setMinFrequency(2.0);
        constraint.setMaxFrequency(8.0);

        AdvancedOptimizationRequest request = new AdvancedOptimizationRequest();
        request.setConsumptionId(10L);
        request.setCarbonReductionPercentageCi(0.1);
        request.setBudgetT(10.0);
        request.setFrequencyConstraints(List.of(constraint));

        AdvancedOptimizationResult result = optimizationService.findOptimalUsageFrequencies(request);

        assertNotNull(result, "Le résultat ne doit pas être null");

        assertEquals("Optimal solution found within constraints.", result.getMessage(), "Le message doit correspondre");
        assertNotNull(result.getOptimizedItems(), "La liste des items optimisés ne doit pas être null");
        assertFalse(result.getOptimizedItems().isEmpty(), "La liste des items optimisés ne doit pas être vide");

        verify(consumptionService).getConsumptionById(10L);
        verify(consumptionService, atLeastOnce()).calculateItemCarbonFootprint(any(), any());
    }
}