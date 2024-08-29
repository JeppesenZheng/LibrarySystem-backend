package src.controller;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestParam String name, @RequestParam String password) {
        try {
            String token = userService.authenticate(name, password);
            if (token != null) {
                User user = userService.getUserByName(name);
                Map<String, String> response = new HashMap<String, String>();
                response.put("token", token);
                response.put("userType", getUserType(user));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("认证失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录时发生错误：" + e.getMessage());
        }
    }

    private String getUserType(User user) {
        if (user instanceof NormalUser) return "normal";
        if (user instanceof BookAdmin) return "bookAdmin";
        if (user instanceof SystemAdmin) return "systemAdmin";
        return "unknown";
    }

}
