package upec.episen.eco.service.OptimizedConsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upec.episen.eco.exceptions.ConsumptionNotFoundException;
import upec.episen.eco.models.consumption.Consumption;
import upec.episen.eco.models.consumption.ConsumptionItem;
import upec.episen.eco.models.optimization.OptimizedConsumptionResult;
import upec.episen.eco.service.consumption.ConsumptionService;

import java.util.*;

@Service
public class OptimizationService {

    @Autowired
    private QuizService quizService;
    @Autowired
    private ConsumptionService consumptionService;



    private double calculateTotalCarbonFootprint(double[] frequencies, List<ConsumptionItem> orderedItems) {
        double totalFootprint = 0.0;
        for (int i = 0; i < orderedItems.size(); i++) {
            ConsumptionItem item = orderedItems.get(i);
            double frequency = frequencies[i];
            totalFootprint += consumptionService.calculateItemCarbonFootprint(//cette methode prends 2 param : le consumption_item concerné ( on va creer un dans la prochene ligne ) et le type d'energie de ce item
                    new ConsumptionItem(item.getName(), item.getMachine(), item.getEnergyInput(), frequency, item.getQuantity(), item.getEnergyType()),// le consumption_item
                    item.getEnergyType()// son type
            );}

        return totalFootprint;
    }



    public OptimizedConsumptionResult optimizeConsumption(Long consumptionId) throws ConsumptionNotFoundException {

        Consumption consumption = consumptionService.getConsumptionById(consumptionId); // l'objet Consumption concerné
        Map<Long, Double> minUsageConstraints = quizService.getMinUsageConstraints(consumptionId);  // Récupérer les contraintes pour la consommation concerné ( voir la classe QuizeService )
        List<ConsumptionItem> orderedItems = consumptionService.getOrderedItemsById(consumptionId); // les consumptions_item de cette consumption ( je sais que je peut juste utiliser getAll mais j'ai utiliser getOrdred parceque je suis sur qu'il marche , getall pas sur haha






        double[] optimizedFrequencies = new double[orderedItems.size()];
        for (int i = 0; i < orderedItems.size(); i++) {
            optimizedFrequencies[i] = orderedItems.get(i).getUsageFrequency();
        }  // optimizedFrequencies est le tableau de stockage des frequences optimisé ( initialement remplie par les frequnces initiales )






        for (int i=0;i < orderedItems.size();i++) {
            ConsumptionItem item = orderedItems.get(i);
            double minFrequency = minUsageConstraints.getOrDefault(item.getId(), 0.0);
            if (optimizedFrequencies[i] > minFrequency) {
                optimizedFrequencies[i] = minFrequency;
            }
        } // on va parcourir les items de la consomation et les reduire le plus possible en se basant sur les contraints ( si le contraint de frequence entré par l'utilisateur d'une consumption_item est moins que la frequence initiale ( c'est pourquoi on a initialiser notre liste par les valeurs initiale ) on va remplacer et optimiser par cette nouvel contraint )





        double initialCarbonFootprint = consumption.getTotalCarbonEmitted();
        double optimizedCarbonFootprint = calculateTotalCarbonFootprint(optimizedFrequencies, orderedItems);
        double howManyWeOptim = initialCarbonFootprint - optimizedCarbonFootprint;



        return new OptimizedConsumptionResult(optimizedCarbonFootprint,optimizedFrequencies, consumption, howManyWeOptim);        // voici l'objet a afficher au front

    }


}