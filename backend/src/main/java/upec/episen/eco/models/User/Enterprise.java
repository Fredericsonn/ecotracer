package upec.episen.eco.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Enterprise extends User {

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private EnterpriseType type;

    // Constructors

    public Enterprise(String username, String phoneNumber, String email, String password, String name, EnterpriseType type) {
        super(username, phoneNumber, email, password);
        this.name = name;
        this.type = type;
    }

    public Enterprise() {

    }

    // Getters - Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnterpriseType getType() {
        return type;
    }

    public void setType(EnterpriseType type) {
        this.type = type;
    }

    // toString
    
    @Override
    public String toString() {
        return "Enterprise [name=" + name + ", type=" + type + "]";
    }

    

}
