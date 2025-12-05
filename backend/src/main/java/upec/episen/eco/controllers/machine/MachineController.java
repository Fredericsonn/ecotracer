package upec.episen.eco.controllers.machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upec.episen.eco.controllers.Helper;
import upec.episen.eco.models.machine.Device;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.models.machine.Vehicle;
import upec.episen.eco.models.machine.enums.UsageCategory;
import upec.episen.eco.service.machine.MachineService;

@RestController
@RequestMapping("/machines")
public class MachineController {

    @Autowired
    private MachineService machineservice;

    @GetMapping
    public ResponseEntity<?> getAllMachines() {
        Map<String, Object> body = new HashMap<String, Object>();
        List<Device> devices = machineservice.getAllDevices();
        List<Vehicle> vehicles = machineservice.getAllVehicles();

        body.put("devices", devices);
        body.put("vehicles", vehicles);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/{category}")
    public List<Machine> getMachinesByUsageCategory(@PathVariable String category) {
        category = Helper.usageCategoryRectifier(category);
        return machineservice.getAllMachinesByUsageCategory(UsageCategory.valueOf(category));
    }
}
