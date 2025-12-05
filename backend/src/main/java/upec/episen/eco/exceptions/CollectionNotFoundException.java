package upec.episen.eco.exceptions;

public class CollectionNotFoundException extends Exception {

    public CollectionNotFoundException(Long id) {
        System.out.println("No collection was found with the id: " + id);
    }
}
