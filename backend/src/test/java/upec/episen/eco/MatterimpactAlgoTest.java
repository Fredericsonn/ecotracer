package upec.episen.eco;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import upec.episen.eco.models.User.Collection;
import upec.episen.eco.models.machine.Algo.MaterialImpact;
import upec.episen.eco.models.machine.Algo.RecyclabilityResult;
import upec.episen.eco.models.machine.Component;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.models.machine.Matter;
import upec.episen.eco.models.machine.Device;
import upec.episen.eco.models.machine.Vehicle;
import upec.episen.eco.models.machine.enums.UsageCategory;
import upec.episen.eco.models.machine.enums.VehicleSize;
import upec.episen.eco.models.machine.enums.VehicleType;
import upec.episen.eco.models.machine.Algo.MatterImpactScore;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MatterImpactAlgoTest {

    private MatterImpactScore matterImpactScore;
    private Machine device;
    private Machine vehicle;
    private Collection collection;

    @BeforeEach
    void setUp() {
        matterImpactScore = new MatterImpactScore();

        Component component1 = new Component("Boîtier");
        component1.addMatter(new Matter("Aluminum", 500.0));
        component1.addMatter(new Matter("Plastic", 200.0));

        Component component2 = new Component("Circuit");
        component2.addMatter(new Matter("Copper", 100.0));
        component2.addMatter(new Matter("Gold", 2.0));
        component2.addMatter(new Matter("Silicon", 50.0));

        Set<Component> deviceComponents = new HashSet<>();
        deviceComponents.add(component1);
        deviceComponents.add(component2);
        device = new Device(1, "Ordinateur", 100.0, UsageCategory.IT, "pc.jpg", deviceComponents);

        for (Component component : deviceComponents) {
            component.setMachine(device);
        }

        Component chassis = new Component("Châssis");
        chassis.addMatter(new Matter("Steel", 5000.0));
        chassis.addMatter(new Matter("Glass", 800.0));

        Component engine = new Component("Moteur");
        engine.addMatter(new Matter("Iron", 1200.0));
        engine.addMatter(new Matter("Aluminum", 800.0));
        engine.addMatter(new Matter("Rubber", 150.0));

        Component interior = new Component("Intérieur");
        interior.addMatter(new Matter("Fabric", 300.0));
        interior.addMatter(new Matter("Polyurethane Foam", 500.0));

        Set<Component> vehicleComponents = new HashSet<>();
        vehicleComponents.add(chassis);
        vehicleComponents.add(engine);
        vehicleComponents.add(interior);

        vehicle = new Vehicle(2, "Voiture", 500.0, UsageCategory.TRANSPORT, "car.jpg",
                vehicleComponents, VehicleSize.MEDIUM, VehicleType.CAR);

        for (Component component : vehicleComponents) {
            component.setMachine(vehicle);
        }

        collection = new Collection();
        Set<Machine> machines = new HashSet<>();
        machines.add(device);
        machines.add(vehicle);
        collection.setMachines(machines);
    }

    @Test
    void testCalculateMatterFootprint() {
        Matter aluminumMatter = new Matter("Aluminum", 1000.0);
        Component testComponent = new Component("Test");
        testComponent.addMatter(aluminumMatter);

        Set<Component> components = new HashSet<>();
        components.add(testComponent);

        Machine testMachine = new Device(100, "Test Machine", 0.0, UsageCategory.IT, "test.jpg", components);

        // Le facteur d'émission pour l'aluminium est de 9.5
        // (1000/1000) * 9.5 = 9.5
        double expected = 9.5;
        double actual = matterImpactScore.calculateMachineFootprint(testMachine);

        assertEquals(expected, actual, 0.01);
    }

    @Test
    void testCalculateMachineFootprint() {
        // Pour le device:
        // Aluminum: (500/1000) * 9.5 = 4.75
        // Plastic: (200/1000) * 4.25 = 0.85
        // Copper: (100/1000) * 4.0 = 0.4
        // Gold: (2/1000) * 18750 = 37.5
        // Silicon: (50/1000) * 8.5 = 0.425
        // + defaultFootprint = 100.0
        // Total: 143.925
        double deviceFootprint = matterImpactScore.calculateMachineFootprint(device);
        assertEquals(143.93, deviceFootprint, 0.01);
    }

    @Test
    void testCalculateTotalFootprint() {
        // Device: 143.93
        // Pour le Vehicle:
        // Steel: (5000/1000) * 2.15 = 10.75
        // Glass: (800/1000) * 1.1 = 0.88
        // Iron: (1200/1000) * 2.1 = 2.52
        // Aluminum: (800/1000) * 9.5 = 7.6
        // Rubber: (150/1000) * 3.6 = 0.54
        // Fabric: (300/1000) * 14.25 = 4.275
        // Polyurethane Foam: (500/1000) * 4.65 = 2.325
        // + defaultFootprint = 500.0
        // Total Vehicle: 528.89
        // Total Collection: 143.93 + 528.89 = 672.82
        double totalFootprint = matterImpactScore.calculateTotalFootprint(collection);
        assertEquals(672.82, totalFootprint, 0.01);
    }

    @Test
    void testEvaluateMachineRecyclability() {
        // Pour le device:
        // Recyclable: Aluminum (500) + Copper (100) + Gold (2) + Silicon (50) = 652
        // Non-recyclable: Plastic (200)
        // Total: 852
        // Recyclabilité: (652/852) * 100 = 76.53%
        RecyclabilityResult deviceResult = matterImpactScore.evaluateMachineRecyclability(device);
        assertEquals(76.53, deviceResult.getRecyclabilityPercentage(), 0.01);

        // Pour le vehicle:
        // Recyclable: Steel (5000) + Glass (800) + Iron (1200) + Aluminum (800) = 7800
        // Non-recyclable: Rubber (150) + Fabric (300) + Polyurethane Foam (500) = 950
        // Total: 8750
        // Recyclabilité: (7800/8750) * 100 = 89.14%
        RecyclabilityResult vehicleResult = matterImpactScore.evaluateMachineRecyclability(vehicle);
        assertEquals(89.14, vehicleResult.getRecyclabilityPercentage(), 0.01);
    }

    @Test
    void testEvaluateCollectionRecyclability() {
        // Device: Recyclable 652, Non-recyclable 200, Total 852
        // Vehicle: Recyclable 7800, Non-recyclable 950, Total 8750
        // Total collection: Recyclable 8452, Total 9602
        // Recyclabilité: (8452/9602) * 100 = 88.02%
        RecyclabilityResult collectionResult = matterImpactScore.evaluateCollectionRecyclability(collection);
        assertEquals(88.02, collectionResult.getRecyclabilityPercentage(), 0.01);
    }
    @Test
    void testCalculateUserScore() {
        // Empreinte carbone totale: 672.82
        // Recyclabilité: 88.02%

        // Calcul du score carbone:
        // totalFootprint = 672.82
        // Entre minCarbonForHighScore (200) et maxCarbonForMidScore (5059.60)
        // carbonScore = 95.0 - ((672.82 - 200.0) * 45.0) / (5059.60 - 200.0) = 95.0 - (472.82 * 45.0 / 4859.6) = 95.0 - 4.39 = 90.61

        // Score final avec pondération:
        // (90.61 * 0.7) + (88.02 * 0.3) = 63.43 + 26.41 = 89.84
        double userScore = matterImpactScore.calculateUserScore(collection);
        assertEquals(89.84, userScore, 0.01);
    }

}