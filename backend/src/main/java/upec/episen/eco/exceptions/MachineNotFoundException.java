package upec.episen.eco.exceptions;

public class MachineNotFoundException extends Exception {

    public MachineNotFoundException(Long id, String type) {
        System.out.println("No " + type + " was found with the id " + id);
    }
}
