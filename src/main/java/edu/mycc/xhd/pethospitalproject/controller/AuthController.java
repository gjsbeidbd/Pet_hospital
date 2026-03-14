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

        return ResponseEntity.ok(response);
    }
}