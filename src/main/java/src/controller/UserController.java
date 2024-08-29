package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.model.User;
import src.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // @PostMapping("/create")
    // public User createUser(@RequestBody User user) {
    //     System.out.println("try to create User class");
    //     try {
    //         return userService.createUser(user.getName(), user.getPassword());
    //     } catch (IllegalArgumentException e) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    //     }
    // }

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
    public ResponseEntity<?> createOtherUser(@RequestBody NormalUser user) {
        try {
            NormalUser normalUser = userService.createNormalUser(user.getName(), user.getPassword());
            return ResponseEntity.ok(normalUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    // example of testing in Client API 
    // http://localhost:8080/users/login?name=user1&password=1111
    public String login(@RequestParam String name, @RequestParam String password) {
        String token = userService.authenticate(name, password);
        if (token != null) {
            return token;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }
}
