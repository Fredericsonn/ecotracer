package upec.episen.eco.repositories.machine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.machine.Device;
import upec.episen.eco.models.machine.enums.UsageCategory;

public interface IDevice extends JpaRepository<Device, Long> {

    List<Device> findAllByUsage(UsageCategory category);
}
