package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.Receptionist;
import edu.mycc.xhd.pethospitalproject.mapper.ReceptionistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceptionistService extends ServiceImpl<ReceptionistMapper, Receptionist> {
    
    @Autowired
    private ReceptionistMapper receptionistMapper;
    
    /**
     * 根据工号查找前台人员
     * @param employeeId 工号
     * @return 前台人员对象
     */
    public Receptionist findByEmployeeId(String employeeId) {
        return receptionistMapper.findByEmployeeId(employeeId);
    }
    
    /**
     * 根据手机号查找前台人员
     * @param phone 手机号
     * @return 前台人员对象
     */
    public Receptionist findByPhone(String phone) {
        return receptionistMapper.findByPhone(phone);
    }
    
    /**
     * 根据邮箱查找前台人员
     * @param email 邮箱
     * @return 前台人员对象
     */
    public Receptionist findByEmail(String email) {
        return receptionistMapper.findByEmail(email);
    }
    
    /**
     * 验证前台人员凭据（支持工号、手机号、邮箱登录）
     * @param username 用户名（可以是工号、手机号或邮箱）
     * @param password 密码
     * @return 前台人员对象（验证失败返回null）
     */
    public Receptionist authenticate(String username, String password) {
        // 先尝试用工号和密码验证
        Receptionist receptionist = receptionistMapper.authenticateByEmployeeIdAndPassword(username, password);
        if (receptionist != null) {
            return receptionist;
        }
        
        // 如果工号验证失败，尝试用手机号或邮箱和密码验证
        receptionist = receptionistMapper.authenticateByPhoneOrEmailAndPassword(username, password);
        return receptionist;
    }
    
    /**
     * 根据手机号或邮箱验证前台人员凭据
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 前台人员对象（验证失败返回null）
     */
    public Receptionist authenticateByPhoneOrEmail(String phoneOrEmail, String password) {
        return receptionistMapper.authenticateByPhoneOrEmailAndPassword(phoneOrEmail, password);
    }
}