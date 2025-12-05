package upec.episen.eco.models.machine;

import java.util.Set;

import jakarta.persistence.Entity;
import upec.episen.eco.models.machine.enums.UsageCategory;


@Entity
public class Device extends Machine {


    public Device(int id, String name, double f, UsageCategory us, String img, Set<Component> r) {
        super(id, name, f, us, img, r);
    }

    public Device() {}

    @Override
    public String getType() {
        return "device";
    }

}
