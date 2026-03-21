package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody Map<String, String> userData) {
        String phone = userData.get("phone");
        String email = userData.get("email");
        String password = userData.get("password");
        String name = userData.get("name");
        String address = userData.get("address");
        String role = userData.get("role") != null ? userData.get("role") : "USER";
        
        User user = userService.registerWithDetails(phone, email, password, name, address, role);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        boolean success = userService.updateUser(id, updatedUser);
        if (success) {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        boolean success;
        if (oldPassword == null || oldPassword.isEmpty()) {
            success = userService.adminChangePassword(id, newPassword);
        } else {
            success = userService.changePassword(id, oldPassword, newPassword);
        }
        
        Map<String, String> response = new HashMap<>();
        
        if (success) {
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "旧密码输入错误，请重新输入！");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();

        if (success) {
            response.put("message", "账户注销成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "账户注销失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/today-new/count")
    public ResponseEntity<Map<String, Object>> getTodayNewUsersCount() {
        int count = userService.getTodayNewUsersCount();
        return ResponseEntity.ok(Map.of("data", count));
    }

    @GetMapping("/total/count")
    public ResponseEntity<Map<String, Object>> getTotalUsersCount() {
        long count = userService.getTotalUsersCount();
        return ResponseEntity.ok(Map.of("data", count));
    }
}