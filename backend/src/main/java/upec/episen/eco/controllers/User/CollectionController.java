package upec.episen.eco.controllers.User;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import upec.episen.eco.exceptions.CollectionNotFoundException;
import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.models.machine.Algo.*;
import upec.episen.eco.models.machine.Component;
import upec.episen.eco.models.machine.Machine;
import upec.episen.eco.models.machine.Matter;
import upec.episen.eco.models.machine.Vehicle;
import upec.episen.eco.models.User.Collection;
import upec.episen.eco.service.User.CollectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping
    public ResponseEntity<?> getAllCollections(@RequestParam(name = "userId", required = false) Long userId) {

        if (userId != null) {
            try {
                return ResponseEntity.ok(collectionService.getAllCollectionsByUser(userId));
            } catch (UserNotFoundException e) {
                return ResponseEntity.internalServerError().body(Map.of("errorMsg", e.getMessage()));
            }
        }
        return ResponseEntity.ok(collectionService.getAllCollections());
    }

    @GetMapping("/{id}/impact")
    public ResponseEntity<Double> getCollectionImpact(@PathVariable Long id) throws CollectionNotFoundException {
        double score = collectionService.calculateCollectionImpact(id);
        return ResponseEntity.ok(score);
    }
    @GetMapping("/{id}/recyclable")
    public ResponseEntity<RecyclabilityResult> getRecyclabilityImpact(@PathVariable Long id) throws CollectionNotFoundException {
        RecyclabilityResult result = collectionService.calculateCollectionRecyclability(id); 
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}/score")
    public ResponseEntity<Double> getScoreImpact(@PathVariable Long id) throws CollectionNotFoundException {
        double result = collectionService.calculateScoreImapct(id);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}/machines/details")
    public ResponseEntity<List<MachineDetailDto>> getMachineDetails(@PathVariable Long id) throws CollectionNotFoundException {
        Collection collection = collectionService.getCollectionById(id);
        List<MachineDetailDto> machineDetails = new ArrayList<>();
        MatterImpactScore calculator = new MatterImpactScore();

        for (Machine machine : collection.getMachines()) {
            MachineDetailDto dto = new MachineDetailDto();
            dto.setId(machine.getId());
            dto.setName(machine.getName());
            dto.setMachineType(machine instanceof Vehicle ? "Vehicle" : "Device");
            dto.setDefaultFootprint(machine.getDefaultFootprint());



            dto.setFootprint(calculator.calculateMachineFootprint(machine));
            dto.setRecyclability(calculator.evaluateMachineRecyclability(machine).getRecyclabilityPercentage());
            dto.setComponents(getComponentsFromMachine(machine));

            machineDetails.add(dto);
        }

        return ResponseEntity.ok(machineDetails);
    }

    private List<ComponentDto> getComponentsFromMachine(Machine machine) {
        List<ComponentDto> components = new ArrayList<>();

        for (Component component : machine.getResources()) {
            ComponentDto componentDto = new ComponentDto();
            componentDto.setId(Math.toIntExact(component.getId()));
            componentDto.setName(component.getName());
            componentDto.setMaterials(getMaterialsFromComponent(component));
            components.add(componentDto);
        }

        return components;
    }

    private List<MaterialDto> getMaterialsFromComponent(Component component) {
        List<MaterialDto> materials = new ArrayList<>();

        for (Matter matter : component.getMatters()) {
            MaterialDto materialDto = new MaterialDto();
            materialDto.setName(matter.getValue());
            materialDto.setVolume(matter.getVolume());

            MaterialImpact materialImpact = MatterImpactScore.MATERIAL_DB.get(matter.getValue());
            if (materialImpact != null) {
                materialDto.setEmissionFactor(materialImpact.getEmissionFactor());
                materialDto.setRecyclable(materialImpact.isRecyclable());
                materialDto.setFootprint((matter.getVolume() / 1000.0) * materialImpact.getEmissionFactor());
            }

            materials.add(materialDto);
        }

        return materials;
    }

}