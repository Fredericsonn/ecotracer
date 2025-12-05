package upec.episen.eco.service.User;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upec.episen.eco.exceptions.CollectionNotFoundException;
import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.models.User.Collection;
import upec.episen.eco.models.User.User;
import upec.episen.eco.models.machine.*;
import upec.episen.eco.models.machine.Algo.MatterImpactScore;
import upec.episen.eco.models.machine.Algo.RecyclabilityResult;
import upec.episen.eco.repositories.User.ICollection;
import upec.episen.eco.repositories.User.IUser;

@Service
public class CollectionService {

    @Autowired
    private ICollection icollection;

    @Autowired
    private IUser iuser;




    public List<Collection> getAllCollections() {
        return icollection.findAll();
    }

    public List<Collection> getAllCollectionsByUser(Long id) throws UserNotFoundException {
        Optional<User> user = iuser.findById(id);

        if (user.isPresent()) return icollection.findAllByUser(user.get());

        else throw new UserNotFoundException(id);
    }

    public Collection getCollectionByUserAndName(Long id, String name) throws UserNotFoundException {
        Optional<User> user = iuser.findById(id);

        if (user.isPresent()) return icollection.findByUserAndName(user.get(), name);

        else throw new UserNotFoundException(id);
    }

    public Collection getCollectionById(Long id) throws CollectionNotFoundException {
        Optional<Collection> collection = icollection.findById(id);

        if (collection.isPresent()) return collection.get();

        throw new CollectionNotFoundException(id);
    }

    public Set<Machine> getCollectionMachines(Collection collection) {
        return collection.getMachines();
    }

    public Collection saveCollection(Collection collection, Long userId) throws UserNotFoundException {
        Optional<User> user = iuser.findById(userId);
        Set<Machine> refinedCollections = new HashSet<>();
        collection.getMachines().forEach(machine -> {
            if (machine instanceof Device) machine = (Device) machine;
            else machine = (Vehicle) machine;
            refinedCollections.add(machine);
        });
        if (user.isPresent()) {
            collection.setUser(user.get());
            collection.setMachines(refinedCollections);
            return icollection.save(collection);
        }

        else throw new UserNotFoundException(userId);
    }

    //=======================================================================================================================================/
    public double calculateCollectionImpact(Long collectionId) throws CollectionNotFoundException {
        // Récupère la collection en fonction de son ID
        Collection collection = getCollectionById(collectionId);

        // Crée une instance de MatterImpactScore pour utiliser la méthode de calcul
        MatterImpactScore matterImpactScore = new MatterImpactScore();

        // Appelle la méthode de calcul de l'impact carbone total pour la collection
        return matterImpactScore.calculateTotalFootprint(collection);  // Calcul de l'empreinte carbone à partir de la collection

    }
    public RecyclabilityResult calculateCollectionRecyclability(Long collectionId) throws CollectionNotFoundException {
        // Récupère la collection en fonction de son ID
        Collection collection = getCollectionById(collectionId);

        // Crée une instance de MatterImpactScore pour utiliser la méthode d'évaluation
        MatterImpactScore matterImpactScore = new MatterImpactScore();

        // Appelle la méthode d'évaluation de la recyclabilité pour la collection
        return matterImpactScore.evaluateCollectionRecyclability(collection); // Correction: utilise la méthode pour Collection
    }
    public double calculateScoreImapct(Long collectionId) throws CollectionNotFoundException {
        // Récupère la collection en fonction de son ID
        Collection collection = getCollectionById(collectionId);

        // Crée une instance de MatterImpactScore pour utiliser la méthode d'évaluation
        MatterImpactScore matterImpactScore = new MatterImpactScore();

        // Appelle la méthode d'évaluation de la recyclabilité pour la collection
        return matterImpactScore.calculateUserScore(collection); // Correction: utilise la méthode pour Collection
    }

}

