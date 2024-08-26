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

    public User createUser(String name, String password) {
        checkUserExists(name, password);
        User user = new User(currentId++, name, password);
        users.put(user.getId(), user);
        return user;
    }

    public SystemAdmin createSystemAdmin(String name, String password) {
        checkUserExists(name, password);
        SystemAdmin admin = new SystemAdmin(currentId++, name, password);
        users.put(admin.getId(), admin);
        return admin;
    }

    public BookAdmin createBookAdmin(String name, String password) {
        checkUserExists(name, password);
        BookAdmin admin = new BookAdmin(currentId++, name, password);
        users.put(admin.getId(), admin);
        return admin;
    }

    public NormalUser createNormalUser(String name, String password) {
        checkUserExists(name, password);
        NormalUser normalUser = new NormalUser(currentId++, name, password);
        users.put(normalUser.getId(), normalUser);
        return normalUser;
    }

    private void checkUserExists(String name, String password) {
        for (User existingUser : users.values()) {
            if (existingUser.getName().equals(name)) {
                throw new IllegalArgumentException("User with the same name already exists.");
            }
            if (existingUser.getPassword().equals(password)) {
                throw new IllegalArgumentException("User with the same password already exists.");
            }
        }
    }
}
