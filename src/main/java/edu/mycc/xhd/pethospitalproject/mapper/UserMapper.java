package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM users WHERE (phone = #{phoneOrEmail} OR email = #{phoneOrEmail}) AND password = #{password} AND role = #{role}")
    User findByPhoneOrEmailAndPasswordAndRole(@Param("phoneOrEmail") String phoneOrEmail, @Param("password") String password, @Param("role") String role);
    
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password} AND role = #{role}")
    User findByUsernameAndPasswordAndRole(@Param("username") String username, @Param("password") String password, @Param("role") String role);
    
    @Select("SELECT * FROM users WHERE (username = #{username} OR email = #{username}) AND password = #{password} AND role = #{role}")
    User findByUsernameOrEmailAndPasswordAndRole(@Param("username") String username, @Param("password") String password, @Param("role") String role);
    
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(@Param("username") String username);
    
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);
    
    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);
}