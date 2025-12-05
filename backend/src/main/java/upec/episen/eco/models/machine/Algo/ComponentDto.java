package upec.episen.eco.models.machine.Algo;

import java.util.List;

public class ComponentDto {
    private int id;
    private String name;
    private List<MaterialDto> materials;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<MaterialDto> getMaterials() { return materials; }
    public void setMaterials(List<MaterialDto> materials) { this.materials = materials; }
}
