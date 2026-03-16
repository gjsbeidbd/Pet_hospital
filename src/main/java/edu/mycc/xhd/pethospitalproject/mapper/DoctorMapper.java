package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    
    /**
     * 根据工号和密码验证医生凭据
     * @param employeeId 工号
     * @param password 密码
     * @return 医生对象，如果验证失败则返回null
     */
    @Select("SELECT * FROM doctors WHERE employee_id = #{employeeId} AND password = #{password}")
    Doctor findByEmployeeIdAndPassword(@Param("employeeId") String employeeId, @Param("password") String password);
    
    /**
     * 根据手机号或邮箱和密码验证医生凭据
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 医生对象，如果验证失败则返回null
     */
    @Select("SELECT * FROM doctors WHERE (phone = #{phoneOrEmail} OR email = #{phoneOrEmail}) AND password = #{password}")
    Doctor findByPhoneOrEmailAndPassword(@Param("phoneOrEmail") String phoneOrEmail, @Param("password") String password);
    
    /**
     * 根据科室删除医生
     */
    @Delete("DELETE FROM doctors WHERE department = #{department}")
    void deleteByDepartment(@Param("department") String department);
}