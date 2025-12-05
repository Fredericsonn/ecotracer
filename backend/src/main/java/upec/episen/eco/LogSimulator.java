package upec.episen.eco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LogSimulator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Random random = new Random();

    // Données de simulation
    private static final List<String> ENTERPRISE_NAMES = Arrays.asList("EcoTech", "GreenSolutions", "RecyclInc", "EnviroCorp", "CleanMaterials");
    private static final List<String> PERSON_NAMES = Arrays.asList("Jean Dupont", "Marie Lefevre", "Pierre Martin", "Sophie Bernard", "Thomas Richard");
    private static final List<String> COLLECTION_NAMES = Arrays.asList("Collection A", "Recyclage Industriel", "Métaux Précieux", "Plastiques", "Électroniques");
    private static final List<String> MACHINE_NAMES = Arrays.asList("Broyeur T1000", "Compacteur C50", "Trieur Optique V2", "Séparateur Magnétique", "Convoyeur X3");
    private static final List<String> MATERIAL_TYPES = Arrays.asList("Acier", "Aluminium", "Plastique", "Verre", "Cuivre", "Carton", "Silicone");
    private static final List<String> LOG_LEVELS = Arrays.asList("INFO", "DEBUG", "DEBUG", "INFO", "DEBUG", "WARN", "INFO");
    private static final List<String> CONTROLLERS = Arrays.asList("UserController", "CollectionController", "MachineService", "ComponentService", "MatterService", "MatterImpactScore", "SecurityFilter");

    public static void main(String[] args) {
        System.out.println("Démarrage du simulateur de logs en continu. Appuyez sur Ctrl+C pour arrêter.");

        try {
            while (true) {
                generateRandomLog();
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println("Simulateur interrompu.");
        }
    }

    private static void log(String source, String level, String action, String message) {
        System.out.println(LocalDateTime.now().format(formatter) + " [" + source + "] " + level + " - " + action + " - " + message);
    }

    private static void generateRandomLog() {
        int type = random.nextInt(10);
        String source = CONTROLLERS.get(random.nextInt(CONTROLLERS.size()));
        String level = LOG_LEVELS.get(random.nextInt(LOG_LEVELS.size()));

        switch (type) {
            case 0:
                generateUserLog(source, level);
                break;
            case 1:
            case 2:
                generateCollectionLog(source, level);
                break;
            case 3:
            case 4:
                generateMachineLog(source, level);
                break;
            case 5:
                generateComponentLog(source, level);
                break;
            case 6:
                generateMatterLog(source, level);
                break;
            case 7:
            case 8:
                generateScoreCalculationLog(source, level);
                break;
            case 9:
                generateSystemLog(source, level);
                break;
        }
    }

    private static void generateUserLog(String source, String level) {
        long userId = 1000 + random.nextInt(3000);
        boolean isEnterprise = random.nextBoolean();
        String action;
        String message;

        int actionType = random.nextInt(5);
        switch (actionType) {
            case 0:
                action = "Création utilisateur";
                if (isEnterprise) {
                    String name = ENTERPRISE_NAMES.get(random.nextInt(ENTERPRISE_NAMES.size()));
                    message = "POST /users/enterprise/post - Création entreprise: " + name + " (id=" + userId + ")";
                } else {
                    String name = PERSON_NAMES.get(random.nextInt(PERSON_NAMES.size()));
                    message = "POST /users/person/post - Création personne: " + name + " (id=" + userId + ")";
                }
                break;
            case 1:
                action = "Récupération utilisateur";
                message = "GET /users/" + (isEnterprise ? "enterprise" : "person") + "/" + userId + " - Récupération des informations";
                break;
            case 2:
                action = "Modification utilisateur";
                message = "PUT /users/put/" + userId + " - Mise à jour des informations";
                break;
            case 3:
                action = "Authentification";
                message = "Tentative d'authentification pour l'utilisateur id=" + userId + " - " + (random.nextBoolean() ? "Succès" : "Échec");
                break;
            default:
                action = "Consultation utilisateurs";
                message = "GET /users?type=" + (isEnterprise ? "enterprise" : "person") + " - Récupération de la liste des utilisateurs";
        }

        log(source, level, action, message);
    }

    private static void generateCollectionLog(String source, String level) {
        long collectionId = 5000 + random.nextInt(2000);
        long userId = 1000 + random.nextInt(3000);
        String collectionName = COLLECTION_NAMES.get(random.nextInt(COLLECTION_NAMES.size()));
        String action;
        String message;

        int actionType = random.nextInt(5);
        switch (actionType) {
            case 0:
                action = "Création collection";
                message = "POST /users/collections/post - Collection '" + collectionName + "' créée pour utilisateur " + userId + " (id=" + collectionId + ")";
                break;
            case 1:
                action = "Récupération collection";
                message = "GET /users/collections/" + collectionName + "?userId=" + userId + " - Récupération des informations";
                break;
            case 2:
                action = "Modification collection";
                message = "PUT /users/collections/put/" + collectionId + " - Mise à jour de la collection '" + collectionName + "'";
                break;
            case 3:
                action = "Suppression collection";
                message = "DELETE /users/collections/delete/" + collectionId + " - Suppression de la collection '" + collectionName + "'";
                break;
            default:
                action = "Consultation collections";
                message = "GET /users/collections?user=" + userId + " - Récupération des collections de l'utilisateur";
        }

        log(source, level, action, message);
    }

    private static void generateMachineLog(String source, String level) {
        long machineId = 8000 + random.nextInt(2000);
        long collectionId = 5000 + random.nextInt(2000);
        String machineName = MACHINE_NAMES.get(random.nextInt(MACHINE_NAMES.size()));
        boolean isVehicle = random.nextBoolean();
        String machineType = isVehicle ? "Vehicle" : "Device";
        String action;
        String message;

        int actionType = random.nextInt(4);
        switch (actionType) {
            case 0:
                action = "Ajout machine";
                message = "Ajout de la machine '" + machineName + "' (type: " + machineType + ") à la collection " + collectionId + " (id=" + machineId + ")";
                break;
            case 1:
                action = "Récupération machine";
                message = "Récupération des détails de la machine " + machineId + " ('" + machineName + "')";
                break;
            case 2:
                action = "Modification machine";
                message = "Mise à jour des informations de la machine " + machineId + " ('" + machineName + "')";
                break;
            default:
                action = "Calcul empreinte";
                double defaultFootprint = 50 + random.nextInt(100);
                double footprint = defaultFootprint * (0.8 + random.nextDouble() * 0.4);
                message = "Calcul de l'empreinte carbone pour la machine " + machineId + " ('" + machineName + "'): " + footprint + " kg CO2e";
        }

        log(source, level, action, message);
    }

    private static void generateComponentLog(String source, String level) {
        long componentId = 10000 + random.nextInt(2000);
        long machineId = 8000 + random.nextInt(2000);
        String componentName = "Composant " + (char)('A' + random.nextInt(5));
        String action;
        String message;

        int actionType = random.nextInt(3);
        switch (actionType) {
            case 0:
                action = "Ajout composant";
                message = "Ajout du composant '" + componentName + "' à la machine " + machineId + " (id=" + componentId + ")";
                break;
            case 1:
                action = "Récupération composant";
                message = "Récupération des détails du composant " + componentId + " ('" + componentName + "')";
                break;
            default:
                action = "Modification composant";
                message = "Mise à jour des informations du composant " + componentId + " ('" + componentName + "')";
        }

        log(source, level, action, message);
    }

    private static void generateMatterLog(String source, String level) {
        long matterId = 20000 + random.nextInt(2000);
        long componentId = 10000 + random.nextInt(2000);
        String material = MATERIAL_TYPES.get(random.nextInt(MATERIAL_TYPES.size()));
        double volume = 100 + random.nextInt(900);
        String action;
        String message;

        int actionType = random.nextInt(3);
        switch (actionType) {
            case 0:
                action = "Ajout matière";
                message = "Ajout de la matière '" + material + "' (volume: " + volume + "g) au composant " + componentId + " (id=" + matterId + ")";
                break;
            case 1:
                action = "Récupération matière";
                message = "Récupération des détails de la matière " + matterId + " ('" + material + "')";
                break;
            default:
                action = "Calcul impact";
                double emissionFactor = 0.5 + random.nextDouble() * 10;
                double impact = (volume / 1000.0) * emissionFactor;
                message = "Calcul de l'impact pour la matière " + matterId + " ('" + material + "'): " + impact + " kg CO2e";
        }

        log(source, level, action, message);
    }

    private static void generateScoreCalculationLog(String source, String level) {
        long collectionId = 5000 + random.nextInt(2000);
        String action;
        String message;

        int actionType = random.nextInt(4);
        switch (actionType) {
            case 0:
                action = "Calcul impact";
                double impactScore = Math.round(random.nextDouble() * 1000) / 10.0;
                message = "GET /api/collection/" + collectionId + "/impact - Score d'impact calculé: " + impactScore;
                break;
            case 1:
                action = "Calcul recyclabilité";
                double recyclabilityPercentage = Math.round(random.nextDouble() * 1000) / 10.0;
                message = "GET /api/collection/" + collectionId + "/recyclable - Recyclabilité calculée: " + recyclabilityPercentage + "%";
                break;
            case 2:
                action = "Calcul score global";
                double globalScore = Math.round(random.nextDouble() * 100) / 10.0;
                message = "GET /api/collection/" + collectionId + "/score - Score global calculé: " + globalScore + "/10";
                break;
            default:
                action = "Calcul détails";
                message = "GET /api/collection/" + collectionId + "/machines/details - Récupération des détails des machines et calcul des scores";
        }

        log(source, level, action, message);
    }

    private static void generateSystemLog(String source, String level) {
        String action;
        String message;

        int actionType = random.nextInt(5);
        switch (actionType) {
            case 0:
                action = "Démarrage";
                message = "Démarrage du service " + source;
                break;
            case 1:
                action = "Performance";
                message = "Temps de réponse moyen: " + (50 + random.nextInt(200)) + "ms";
                break;
            case 2:
                action = "Connexion BD";
                message = "Connexion à la base de données " + (random.nextBoolean() ? "établie" : "renouvelée");
                break;
            case 3:
                action = "Accès";
                message = "Accès " + (random.nextBoolean() ? "autorisé" : "refusé") + " à l'utilisateur " + (1000 + random.nextInt(3000));
                break;
            default:
                action = "Maintenance";
                message = "Opération de maintenance programmée: " + (random.nextBoolean() ? "nettoyage des caches" : "optimisation des requêtes");
        }

        log(source, level, action, message);
    }
}