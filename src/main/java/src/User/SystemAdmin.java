package src.User;

import jakarta.persistence.*;

import src.model.User;

@Entity
@DiscriminatorValue("SYSTEM")
public class SystemAdmin extends User {

    public SystemAdmin() {
        super();
    }

    public SystemAdmin(String name, String password) {
        super(name, password);
    }

}
