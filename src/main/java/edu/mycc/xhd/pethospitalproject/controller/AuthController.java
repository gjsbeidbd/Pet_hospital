package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import edu.mycc.xhd.pethospitalproject.entity.Director;
import edu.mycc.xhd.pethospitalproject.entity.Receptionist;
import edu.mycc.xhd.pethospitalproject.service.UserService;
import edu.mycc.xhd.pethospitalproject.service.DoctorService;
import edu.mycc.xhd.pethospitalproject.service.DirectorService;
import edu.mycc.xhd.pethospitalproject.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private DirectorService directorService;
    
    @Autowired
    private ReceptionistService receptionistService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String role = loginRequest.get("role");

        // 根据不同角色验证凭据
        switch (role) {
            case "USER":
                // 验证用户凭据（手机号或邮箱登录）
                User user = userService.authenticate(username, password, role);
                if (user != null) {
                    return createLoginResponse(user.getId(), role);
                }
                break;
                
            case "RECEPTIONIST":
                // 直接使用手机号或邮箱登录
                Receptionist receptionist = receptionistService.authenticateByPhoneOrEmail(username, password);
                if (receptionist != null) {
                    return createLoginResponse(receptionist.getId(), role);
                }
                break;
                
            case "DOCTOR":
                // 直接使用手机号或邮箱登录
                Doctor doctor = doctorService.authenticateByPhoneOrEmail(username, password);
                if (doctor != null) {
                    return createLoginResponse(doctor.getId(), role);
                }
                break;
                
            case "ADMIN":
                // 直接使用手机号或邮箱登录
                Director director = directorService.authenticateByPhoneOrEmail(username, password);
                if (director != null) {
                    return createLoginResponse(director.getId(), role);
                }
                break;
                
            default:
                break;
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String role = registerRequest.get("role");
        if (role == null || role.isEmpty()) {
            role = "USER";
        }

        boolean success = userService.register(username, password, role);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Registration successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));
        }
    }
    
    /**
     * 创建登录响应
     * @param userId 用户ID
     * @param role 角色
     * @return 响应对象
     */
    private ResponseEntity<Map<String, Object>> createLoginResponse(Long userId, String role) {
        // 生成模拟token（实际项目中应该使用JWT）
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);
        response.put("userId", userId);
        
        // 如果是前台人员，返回科室信息
        if ("RECEPTIONIST".equals(role)) {
            Receptionist receptionist = receptionistService.getById(userId);
            if (receptionist != null && receptionist.getDepartment() != null) {
                response.put("department", receptionist.getDepartment());
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/receptionist/{id}")
    public ResponseEntity<Receptionist> getReceptionistInfo(@PathVariable Long id) {
        Receptionist receptionist = receptionistService.getById(id);
        if (receptionist != null) {
            return ResponseEntity.ok(receptionist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/receptionist/{id}")
    public ResponseEntity<Map<String, String>> updateReceptionistInfo(@PathVariable Long id, @RequestBody Receptionist receptionist) {
        receptionist.setId(id);
        boolean success = receptionistService.updateById(receptionist);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "个人信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "个人信息更新失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/receptionist/{id}/password")
    public ResponseEntity<Map<String, String>> changeReceptionistPassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        Map<String, String> response = new HashMap<>();
        boolean success = receptionistService.changePassword(id, oldPassword, newPassword);
        if (success) {
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "旧密码输入错误，请重新输入");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<Doctor> getDoctorInfo(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/doctor/{id}")
    public ResponseEntity<Map<String, String>> updateDoctorInfo(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        boolean success = doctorService.updateById(doctor);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "个人信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "个人信息更新失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/doctor/{id}/password")
    public ResponseEntity<Map<String, String>> changeDoctorPassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        Map<String, String> response = new HashMap<>();
        boolean success = doctorService.changePassword(id, oldPassword, newPassword);
        if (success) {
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "旧密码输入错误，请重新输入");
            return ResponseEntity.badRequest().body(response);
        }
    }
}