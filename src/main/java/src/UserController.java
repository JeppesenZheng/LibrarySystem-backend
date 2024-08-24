package src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.User.User;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        System.out.println("try to create User class");
        try {
            return userService.createUser(user.getName(), user.getPassword());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/createSystemAdmin")
    public SystemAdmin createSystemAdmin(@RequestBody User user) {
        System.out.println("try to create admin user");
        try {
            return userService.createSystemAdmin(user.getName(), user.getPassword());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/createBookAdmin")
    public BookAdmin createBookAdmin(@RequestBody User user) {
        try {
            return userService.createBookAdmin(user.getName(), user.getPassword());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/createNormalUser")
    public NormalUser createOtherUser(@RequestBody User user) {
        try {
            return userService.createNormalUser(user.getName(), user.getPassword());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, @RequestParam String password) {
        String token = userService.authenticate(name, password);
        if (token != null) {
            return token;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }
}
