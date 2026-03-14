package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class AvatarController {

    @Autowired
    private UserService userService;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    /**
     * 上传用户头像
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
    
    Map<String, String> response = new HashMap<>();
    
    // 检查文件是否为空
    if (file.isEmpty()) {
        response.put("error", "上传文件不能为空");
        return ResponseEntity.badRequest().body(response);
    }

    // 检查文件类型
    String contentType = file.getContentType();
    if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
        response.put("error", "只支持JPG和PNG格式的图片");
        return ResponseEntity.badRequest().body(response);
    }

    // 检查文件大小（限制为2MB）
    if (file.getSize() > 2 * 1024 * 1024) {
        response.put("error", "图片大小不能超过2MB");
        return ResponseEntity.badRequest().body(response);
    }

    try {
        // 创建上传目录
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = uploadDir.resolve(uniqueFilename);
        file.transferTo(filePath);

        // 返回头像URL，但不立即更新用户信息
        String imageUrl = "/uploads/" + uniqueFilename;
        response.put("imageUrl", imageUrl);
        response.put("message", "头像上传成功");
        return ResponseEntity.ok(response);
    } catch (IOException e) {
        e.printStackTrace();
        response.put("error", "文件上传失败: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
}