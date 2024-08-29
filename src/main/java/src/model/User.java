package src.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.*;
import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NormalUser.class, name = "normal"),
    @JsonSubTypes.Type(value = BookAdmin.class, name = "bookAdmin"),
    @JsonSubTypes.Type(value = SystemAdmin.class, name = "systemAdmin")
})
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String password;

    // Constructors

    public User() { }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
