package src;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.User.User;

@Service
public class UserService {
    private Map<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    public User createUser(String name, String email) {
        checkUserExists(name, email);
        User user = new User(currentId++, name, email);
        users.put(user.getId(), user);
        return user;
    }

    public SystemAdmin createSystemAdmin(String name, String email) {
        checkUserExists(name, email);
        SystemAdmin admin = new SystemAdmin(currentId++, name, email);
        users.put(admin.getId(), admin);
        return admin;
    }

    public BookAdmin createBookAdmin(String name, String email) {
        checkUserExists(name, email);
        BookAdmin admin = new BookAdmin(currentId++, name, email);
        users.put(admin.getId(), admin);
        return admin;
    }

    public NormalUser createNormalUser(String name, String email) {
        checkUserExists(name, email);
        NormalUser normalUser = new NormalUser(currentId++, name, email);
        users.put(normalUser.getId(), normalUser);
        return normalUser;
    }

    private void checkUserExists(String name, String email) {
        for (User existingUser : users.values()) {
            if (existingUser.getName().equals(name)) {
                throw new IllegalArgumentException("User with the same name already exists.");
            }
            if (existingUser.getEmail().equals(email)) {
                throw new IllegalArgumentException("User with the same email already exists.");
            }
        }
    }
}
