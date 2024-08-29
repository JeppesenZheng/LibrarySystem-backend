package src.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Value("${jwt.secret}")
    private String secretString;

    // unused
    // private Key getSigningKey() {
    //     byte[] keyBytes = java.util.Base64.getDecoder().decode(secretString);
    //     return Keys.hmacShaKeyFor(keyBytes);
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
    public ResponseEntity<?> login(@RequestParam String name, @RequestParam String password) {
        try {
            String token = userService.authenticate(name, password);
            if (token != null) {
                User user = userService.getUserByName(name);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("userType", getUserType(user));
                System.out.println("Generated token: " + token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("认证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录时发生错误：" + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            User user = userService.getUserByName(username);
            if (!(user instanceof SystemAdmin)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("没有权限访问此资源");
            }
            List<Map<String, Object>> allUsers = userService.getAllUsers();
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取用户列表失败: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/type")
    public ResponseEntity<?> updateUserType(@PathVariable Long userId, @RequestParam String newUserType, @RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            User admin = userService.getUserByName(username);
            if (!(admin instanceof SystemAdmin)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("没有权限执行此操作");
            }
            User updatedUser = userService.updateUserType(userId, newUserType);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新用户类型失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            User admin = userService.getUserByName(username);
            if (!(admin instanceof SystemAdmin)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("没有权限执行此操作");
            }
            userService.deleteUser(userId);
            return ResponseEntity.ok("用户已成功删除");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除用户失败: " + e.getMessage());
        }
    }

    private String getUserType(User user) {
        if (user instanceof NormalUser) return "normal";
        if (user instanceof BookAdmin) return "bookAdmin";
        if (user instanceof SystemAdmin) return "systemAdmin";
        return "unknown";
    }

}
