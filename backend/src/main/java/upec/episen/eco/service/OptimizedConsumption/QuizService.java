package upec.episen.eco.service.OptimizedConsumption;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {
    private  Map<Long, Map<Long, Double>> consumptionConstraints = new ConcurrentHashMap<>(); // ce n'est pas de AI, Le clé est l'ID de la consomation concrné, la valeur est un map d'ID de chaque consumption_item et sa contraint minimale




    public void saveConstraints(Long consumptionId, Map<Long, Double> constraints) {
        consumptionConstraints.put(consumptionId, constraints);}

    public Map<Long, Double> getMinUsageConstraints(Long consumptionId) {
        return consumptionConstraints.getOrDefault(consumptionId, new HashMap<>()); }


}

