package src.User;
import jakarta.persistence.*;

import src.model.User;

@Entity
@DiscriminatorValue("NORMAL")
public class NormalUser extends User {

    public NormalUser() { }
    public NormalUser(String name, String password) {
        super(name, password);
    }

}
