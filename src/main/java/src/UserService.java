package src;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private Map<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    public User createUser(String name, String email) {
        for (User existingUser : users.values()) {
            if (existingUser.getName().equals(name)) {
                throw new IllegalArgumentException("User with the same name already exists.");
            }
            if (existingUser.getEmail().equals(email)) {
                throw new IllegalArgumentException("User with the same email already exists.");
            }
        }
        User user = new User(currentId++, name, email);
        users.put(user.getId(), user);
        return user;
    }
}
