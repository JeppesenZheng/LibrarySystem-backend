package src.User;
import jakarta.persistence.*;

import src.model.User;

@Entity
@DiscriminatorValue("BOOK")
public class BookAdmin extends User {

    public BookAdmin() {
        super();
    }

    public BookAdmin(String name, String password) {
        super(name, password);
    }
    
}
