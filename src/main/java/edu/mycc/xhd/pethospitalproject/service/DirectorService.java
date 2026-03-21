package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.Director;
import edu.mycc.xhd.pethospitalproject.mapper.DirectorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectorService {
    
    @Autowired
    private DirectorMapper directorMapper;
    
    /**
     * 验证院长凭据
     * @param employeeId 工号
     * @param password 密码
     * @return 院长对象，如果验证失败则返回null
     */
    public Director authenticate(String employeeId, String password) {
        return directorMapper.findByEmployeeIdAndPassword(employeeId, password);
    }
    
    /**
     * 验证院长凭据（手机号或邮箱登录）
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 院长对象，如果验证失败则返回null
     */
    public Director authenticateByPhoneOrEmail(String phoneOrEmail, String password) {
        return directorMapper.findByPhoneOrEmailAndPassword(phoneOrEmail, password);
    }
    
    /**
     * 根据 ID 获取院长
     * @param id 院长 ID
     * @return 院长对象
     */
    public Director getDirectorById(Long id) {
        return directorMapper.selectById(id);
    }
    
    /**
     * 更新院长信息
     * @param id 院长 ID
     * @param director 新的院长信息
     * @return 更新后的院长对象
     */
    public Director updateDirector(Long id, Director director) {
        Director existingDirector = directorMapper.selectById(id);
        if (existingDirector == null) {
            return null;
        }
        
        // 更新允许修改的字段
        if (director.getName() != null) {
            existingDirector.setName(director.getName());
        }
        if (director.getBirthday() != null) {
            existingDirector.setBirthday(director.getBirthday());
        }
        if (director.getIdCard() != null) {
            existingDirector.setIdCard(director.getIdCard());
        }
        if (director.getAddress() != null) {
            existingDirector.setAddress(director.getAddress());
        }
        if (director.getPhone() != null) {
            existingDirector.setPhone(director.getPhone());
        }
        if (director.getEmail() != null) {
            existingDirector.setEmail(director.getEmail());
        }
        if (director.getImage() != null) {
            existingDirector.setImage(director.getImage());
        }
        // 兼容 joinDate 和 hireDate 字段名
        if (director.getJoinDate() != null) {
            existingDirector.setHireDate(director.getJoinDate());
        } else if (director.getHireDate() != null) {
            existingDirector.setHireDate(director.getHireDate());
        }
        
        directorMapper.updateById(existingDirector);
        return existingDirector;
    }
    
    /**
     * 修改院长密码
     * @param id 院长 ID
     * @param newPassword 新密码
     * @return 更新后的院长对象
     */
    public Director changePassword(Long id, String newPassword) {
        Director director = directorMapper.selectById(id);
        if (director == null) {
            return null;
        }
        
        director.setPassword(newPassword);
        directorMapper.updateById(director);
        return director;
    }
}