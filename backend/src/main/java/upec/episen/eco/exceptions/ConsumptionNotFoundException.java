package upec.episen.eco.exceptions;

public class ConsumptionNotFoundException extends Exception {

    public ConsumptionNotFoundException(Long id) {
        System.out.println("No consumption was found with the id: " + id);
    }
}
