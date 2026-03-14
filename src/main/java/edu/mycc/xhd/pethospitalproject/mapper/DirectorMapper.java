package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Director;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DirectorMapper extends BaseMapper<Director> {
    
    /**
     * 根据工号和密码验证院长凭据
     * @param employeeId 工号
     * @param password 密码
     * @return 院长对象，如果验证失败则返回null
     */
    @Select("SELECT * FROM directors WHERE employee_id = #{employeeId} AND password = #{password}")
    Director findByEmployeeIdAndPassword(@Param("employeeId") String employeeId, @Param("password") String password);
    
    /**
     * 根据手机号或邮箱和密码验证院长凭据
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 院长对象，如果验证失败则返回null
     */
    @Select("SELECT * FROM directors WHERE (phone = #{phoneOrEmail} OR email = #{phoneOrEmail}) AND password = #{password}")
    Director findByPhoneOrEmailAndPassword(@Param("phoneOrEmail") String phoneOrEmail, @Param("password") String password);
}