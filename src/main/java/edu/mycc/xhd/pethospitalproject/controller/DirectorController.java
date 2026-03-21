package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Director;
import edu.mycc.xhd.pethospitalproject.service.DirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/directors")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable Long id) {
        Director director = directorService.getDirectorById(id);
        if (director != null) {
            return ResponseEntity.ok(director);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 更新院长信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Director> updateDirector(@PathVariable Long id, @RequestBody Director director) {
        Director updatedDirector = directorService.updateDirector(id, director);
        if (updatedDirector != null) {
            return ResponseEntity.ok(updatedDirector);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 修改院长密码
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        // 验证旧密码
        Director director = directorService.getDirectorById(id);
        if (director == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "院长不存在");
            return ResponseEntity.status(404).body(errorResponse);
        }
        
        if (!director.getPassword().equals(oldPassword)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "旧密码输入错误，请重新输入！");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        // 更新密码
        Director updatedDirector = directorService.changePassword(id, newPassword);
        if (updatedDirector != null) {
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "密码修改成功");
            successResponse.put("data", updatedDirector);
            return ResponseEntity.ok(successResponse);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "密码修改失败");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}