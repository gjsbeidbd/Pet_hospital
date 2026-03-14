package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Receptionist;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ReceptionistMapper extends BaseMapper<Receptionist> {
    
    /**
     * 根据工号查找前台人员
     * @param employeeId 工号
     * @return 前台人员对象
     */
    @Select("SELECT * FROM receptionists WHERE employee_id = #{employeeId} AND status = 'active'")
    Receptionist findByEmployeeId(@Param("employeeId") String employeeId);
    
    /**
     * 根据手机号查找前台人员
     * @param phone 手机号
     * @return 前台人员对象
     */
    @Select("SELECT * FROM receptionists WHERE phone = #{phone} AND status = 'active'")
    Receptionist findByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查找前台人员
     * @param email 邮箱
     * @return 前台人员对象
     */
    @Select("SELECT * FROM receptionists WHERE email = #{email} AND status = 'active'")
    Receptionist findByEmail(@Param("email") String email);
    
    /**
     * 根据工号和密码验证前台人员
     * @param employeeId 工号
     * @param password 密码
     * @return 前台人员对象
     */
    @Select("SELECT * FROM receptionists WHERE employee_id = #{employeeId} AND password = #{password} AND status = 'active'")
    Receptionist authenticateByEmployeeIdAndPassword(@Param("employeeId") String employeeId, @Param("password") String password);
    
    /**
     * 根据手机号或邮箱和密码验证前台人员
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 前台人员对象
     */
    @Select("SELECT * FROM receptionists WHERE (phone = #{phoneOrEmail} OR email = #{phoneOrEmail}) AND password = #{password} AND status = 'active'")
    Receptionist authenticateByPhoneOrEmailAndPassword(@Param("phoneOrEmail") String phoneOrEmail, @Param("password") String password);
}