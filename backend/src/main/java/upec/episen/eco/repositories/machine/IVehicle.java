package upec.episen.eco.repositories.machine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.machine.Vehicle;
import upec.episen.eco.models.machine.enums.UsageCategory;

public interface IVehicle extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findAllByUsage(UsageCategory category);
}
