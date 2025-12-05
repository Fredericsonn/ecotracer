package upec.episen.eco.service.consumption;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.exceptions.MachineNotFoundException;
import upec.episen.eco.models.User.User;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.consumption.enums.EnergyType;
import upec.episen.eco.models.machine.Device;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.repositories.consumption.IConsumption;
import upec.episen.eco.repositories.temp.IAlgoParam;
import upec.episen.eco.service.User.UserService;
import upec.episen.eco.service.machine.MachineService;
import upec.episen.eco.service.norms.EmissionFactorService;

public class ConsumptionServiceTest {

    @Mock
    private IConsumption consumptionRepo;
    @Mock
    private UserService userService;
    @Mock
    private EmissionFactorService emissionFactorService;
    @Mock
    private MachineService machineService;
    @Mock
    private IAlgoParam algoParamRepo;

    @InjectMocks
    private ConsumptionService consumptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllConsumptionsByUser() throws UserNotFoundException {
        User mockedUser = new User();
        mockedUser.setId(1L);
        when(userService.getUserById(1L)).thenReturn(mockedUser);

        Consumption consumption = new Consumption();
        when(consumptionRepo.findAllByUser(mockedUser)).thenReturn(List.of(consumption));

        List<Consumption> result = consumptionService.getAllConsumptionsByUser(1L);

        assertEquals(1, result.size());
        verify(userService).getUserById(1L);
        verify(consumptionRepo).findAllByUser(mockedUser);
    }

    @Test
    void testCalculateItemCarbonFootprintForDevice() {
        Device device = new Device();
        ConsumptionItem item = new ConsumptionItem();
        item.setMachine(device);
        item.setEnergyInput(500); // watts
        item.setUsageFrequency(2); // hours
        item.setEnergyType(EnergyType.ELECTRICITY);

        when(emissionFactorService.getEmissionFactor(EnergyType.ELECTRICITY, null, null)).thenReturn(0.5);

        double result = consumptionService.calculateItemCarbonFootprint(item, EnergyType.ELECTRICITY);

        assertEquals(0.5, result);
    }

    @Test
    void testSaveConsumptionSetsTotalCarbonAndDate() throws MachineNotFoundException {
        Device device = mock(Device.class);
        when(device.getId()).thenReturn(1);
        when(device.getType()).thenReturn("Device");

        ConsumptionItem item = new ConsumptionItem();
        item.setMachine(device);
        item.setEnergyInput(500);
        item.setUsageFrequency(2);
        item.setEnergyType(EnergyType.ELECTRICITY);

        Consumption consumption = new Consumption();
        consumption.setConsumptionItems(Set.of(item));

        when(machineService.findById(1L, "Device")).thenReturn(device);
        when(emissionFactorService.getEmissionFactor(EnergyType.ELECTRICITY, null, null)).thenReturn(0.5);
        when(consumptionRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Consumption saved = consumptionService.saveConsumption(consumption);

        assertEquals(0.5, saved.getTotalCarbonEmitted());

    }

    @Test
    void testManufacturingImpactRatioCalculator() {
        ConsumptionItem item = new ConsumptionItem();
        Machine mockMachine = mock(Machine.class);
        item.setMachine(mockMachine);
        item.setCarbonFootprint(100);
        item.setId(1L);

        Consumption consumption = new Consumption();
        consumption.setConsumptionItems(Set.of(item));
        consumption.setTotalCarbonEmitted(50);

        double totalEmissions = 50 * 365;
        double mockManufacturingImpact = 500;

        ConsumptionService spyService = spy(consumptionService);
        doReturn(mockManufacturingImpact).when(spyService).calculateConsumptionImpactScore(consumption);

        double result = spyService.manufacturingImpactRatioCalculator(consumption);
        double expected = mockManufacturingImpact / (mockManufacturingImpact + totalEmissions);

        assertEquals(expected, result);
    }
}
