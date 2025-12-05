package upec.episen.eco.models.machine.Algo;

import java.util.HashMap;
import java.util.Map;

import upec.episen.eco.models.User.Collection;
import upec.episen.eco.models.machine.Component;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.models.machine.Matter;

public class MatterImpactScore {

       public static final Map<String, MaterialImpact> MATERIAL_DB = new HashMap<>();

       static {
              // Matériaux recyclables (1)
              MATERIAL_DB.put("Galvanized Steel", new MaterialImpact(2.4, true));
              MATERIAL_DB.put("Glass", new MaterialImpact(1.1, true));
              MATERIAL_DB.put("Gold", new MaterialImpact(18750.0, true));
              MATERIAL_DB.put("Copper", new MaterialImpact(4.0, true));
              MATERIAL_DB.put("Aluminum", new MaterialImpact(9.5, true));
              MATERIAL_DB.put("Steel", new MaterialImpact(2.15, true));
              MATERIAL_DB.put("Iron", new MaterialImpact(2.1, true));
              MATERIAL_DB.put("Silver", new MaterialImpact(5.0, true));
              MATERIAL_DB.put("Lead", new MaterialImpact(4.5, true));
              MATERIAL_DB.put("Zinc", new MaterialImpact(3.4, true));
              MATERIAL_DB.put("Bamboo", new MaterialImpact(0.1, true));
              MATERIAL_DB.put("Cobalt", new MaterialImpact(10.0, true));
              MATERIAL_DB.put("Stainless Steel", new MaterialImpact(5.25, true));
              MATERIAL_DB.put("Nickel", new MaterialImpact(8.75, true));
              MATERIAL_DB.put("Magnesium", new MaterialImpact(22.0, true));
              MATERIAL_DB.put("Polycarbonate", new MaterialImpact(6.1, true));
              MATERIAL_DB.put("Maple Wood", new MaterialImpact(0.3, true));
              MATERIAL_DB.put("Titanium", new MaterialImpact(11.0, true));
              MATERIAL_DB.put("Cotton", new MaterialImpact(0.5, true));
              MATERIAL_DB.put("ABS Plastic", new MaterialImpact(3.85, true));
              MATERIAL_DB.put("Tempered Glass", new MaterialImpact(1.5, true));
              MATERIAL_DB.put("Ceramic", new MaterialImpact(2.25, true));
              MATERIAL_DB.put("Neodymium Magnets", new MaterialImpact(47.5, true));
              MATERIAL_DB.put("Silicon", new MaterialImpact(8.5, true));
              MATERIAL_DB.put("Lithium", new MaterialImpact(25.0, true));

              // Matériaux partiellement recyclables ou difficiles à recycler (0)
              MATERIAL_DB.put("Rubber (tires)", new MaterialImpact(3.6, false));
              MATERIAL_DB.put("Plastic", new MaterialImpact(4.25, false));
              MATERIAL_DB.put("Wood (general)", new MaterialImpact(0.5, false));
              MATERIAL_DB.put("Fiberglass", new MaterialImpact(3.15, false));
              MATERIAL_DB.put("Rare Earths", new MaterialImpact(37.5, false));
              MATERIAL_DB.put("Rubber", new MaterialImpact(3.6, false));

              // Matériaux difficilement recyclables (0)
              MATERIAL_DB.put("Tungsten", new MaterialImpact(10.0, false));
              MATERIAL_DB.put("Glass Fiber", new MaterialImpact(2.0, false));
              MATERIAL_DB.put("Carbon Fiber", new MaterialImpact(24.0, false));
              MATERIAL_DB.put("Wool", new MaterialImpact(1.5, false));
              MATERIAL_DB.put("Polyester", new MaterialImpact(5.0, false));
              MATERIAL_DB.put("Polyurethane Foam", new MaterialImpact(4.65, false));
              MATERIAL_DB.put("Teflon Coating", new MaterialImpact(6.75, false));
              MATERIAL_DB.put("Polyurethane", new MaterialImpact(4.35, false));
              MATERIAL_DB.put("Leather", new MaterialImpact(20.0, false));
              MATERIAL_DB.put("Fabric", new MaterialImpact(14.25, false));
              MATERIAL_DB.put("Ink", new MaterialImpact(3.75, false));
              MATERIAL_DB.put("Ferrite", new MaterialImpact(3.0, false));
       }
       public double calculateMachineFootprint(Machine machine) {
              double totalFootprint = 0.0;

              for (Component component : machine.getResources()) {
                     for (Matter matter : component.getMatters()) {
                            double matterFootprint = calculateMatterFootprint(matter);
                            totalFootprint += matterFootprint;
                     }
              }

              totalFootprint += machine.getDefaultFootprint();

              return totalFootprint;
       }
       public double calculateMachineFootprint2(Machine machine) {
              String machineType = machine.getName(); 

              switch (machineType) {
                     case "HPC System":
                            return 350180.564;
                     case "Storage System":
                            return 70790.65;
                     case "HVAC System":
                            return 100317.56;
                     case "Server":
                            return 802.370;
                     case "Data Center":
                            return 1001209.53;
                     default:
                            
                            double totalFootprint = 0.0;

                            for (Component component : machine.getResources()) {
                                   for (Matter matter : component.getMatters()) {
                                          double matterFootprint = calculateMatterFootprint(matter);
                                          totalFootprint += matterFootprint;
                                   }
                            }

                            totalFootprint += machine.getDefaultFootprint();
                            return totalFootprint * 10;
              }
       }

       private double calculateMatterFootprint(Matter matter) {
              MaterialImpact materialImpact = MATERIAL_DB.get(matter.getValue());

              if (materialImpact == null) {
                     return 0.0;
              }

              return (matter.getVolume() / 1000.0) * materialImpact.getEmissionFactor();
       }

       public double calculateTotalFootprint(Collection collection) {
              double totalFootprint = 0.0;

              for (Machine machine : collection.getMachines()) {
                     totalFootprint += calculateMachineFootprint(machine);
              }

              return totalFootprint;
       }

       public RecyclabilityResult evaluateMachineRecyclability(Machine machine) {
              double totalMass = 0.0;
              double recyclableMass = 0.0;

              for (Component component : machine.getResources()) {
                     for (Matter matter : component.getMatters()) {
                            double matterMass = matter.getVolume();
                            totalMass += matterMass;

                            MaterialImpact materialImpact = MATERIAL_DB.get(matter.getValue());

                            if (materialImpact != null && materialImpact.isRecyclable()) {
                                   recyclableMass += matterMass;
                            }
                     }
              }

              double recyclabilityPercentage = (totalMass > 0) ? (recyclableMass / totalMass) * 100 : 0;

              return new RecyclabilityResult(recyclabilityPercentage);
       }

       public RecyclabilityResult evaluateCollectionRecyclability(Collection collection) {
              double totalMass = 0.0;
              double recyclableMass = 0.0;

              for (Machine machine : collection.getMachines()) {
                     for (Component component : machine.getResources()) {
                            for (Matter matter : component.getMatters()) {
                                   double matterMass = matter.getVolume();
                                   totalMass += matterMass;

                                   MaterialImpact materialImpact = MATERIAL_DB.get(matter.getValue());

                                   if (materialImpact != null && materialImpact.isRecyclable()) {
                                          recyclableMass += matterMass;
                                   }
                            }
                     }
              }

              double recyclabilityPercentage = (totalMass > 0) ? (recyclableMass / totalMass) * 100 : 0;

              return new RecyclabilityResult(recyclabilityPercentage);
       }

       public double calculateUserScore(Collection collection) {
              double totalFootprint = calculateTotalFootprint(collection);
              RecyclabilityResult recyclability = evaluateCollectionRecyclability(collection);
              double recyclabilityPercentage = recyclability.getRecyclabilityPercentage();

              double minCarbonForHighScore = 200.0;
              double maxCarbonForMidScore = 5059.60;
              double maxCarbonForLowScore1 = 10000.0;
              double maxCarbonForLowScore2 = 30000.0;

              double carbonScore;

              if (totalFootprint >= maxCarbonForLowScore2) {
                     carbonScore = 0.0;
              } else if (totalFootprint >= maxCarbonForLowScore1) {
                     carbonScore = 30.0 - ((totalFootprint - maxCarbonForLowScore1) * 20.0)
                                   / (maxCarbonForLowScore2 - maxCarbonForLowScore1);
              } else if (totalFootprint >= maxCarbonForMidScore) {
                     carbonScore = 50.0 - ((totalFootprint - maxCarbonForMidScore) * 20.0)
                                   / (maxCarbonForLowScore1 - maxCarbonForMidScore);
              } else if (totalFootprint >= minCarbonForHighScore) {
                     carbonScore = 95.0 - ((totalFootprint - minCarbonForHighScore) * 45.0)
                                   / (maxCarbonForMidScore - minCarbonForHighScore);
              } else {
                     carbonScore = 95.0;
              }

              // Pondération
              double carbonWeight = 0.7;
              double recyclabilityWeight = 0.3;

              double score = (carbonScore * carbonWeight)
                            + (recyclabilityPercentage * recyclabilityWeight);

              // Garantir score entre 0 et 100
              return Math.min(Math.max(score, 0), 100);
       }

       public boolean isMaterialRecyclable(String materialName) {
              MaterialImpact materialImpact = MATERIAL_DB.get(materialName);
              return materialImpact != null && materialImpact.isRecyclable();
       }
}