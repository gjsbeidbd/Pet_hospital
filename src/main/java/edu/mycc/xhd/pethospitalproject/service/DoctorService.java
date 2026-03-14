package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    /**
     * 验证医生凭据
     * @param employeeId 工号
     * @param password 密码
     * @return 医生对象，如果验证失败则返回null
     */
    public Doctor authenticate(String employeeId, String password) {
        return doctorMapper.findByEmployeeIdAndPassword(employeeId, password);
    }
    
    /**
     * 验证医生凭据（手机号或邮箱登录）
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 医生对象，如果验证失败则返回null
     */
    public Doctor authenticateByPhoneOrEmail(String phoneOrEmail, String password) {
        return doctorMapper.findByPhoneOrEmailAndPassword(phoneOrEmail, password);
    }
    
    /**
     * 根据ID获取医生
     * @param id 医生ID
     * @return 医生对象
     */
    public Doctor getDoctorById(Long id) {
        return doctorMapper.selectById(id);
    }
}