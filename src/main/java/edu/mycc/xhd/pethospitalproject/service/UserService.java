package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 验证用户凭据
     *
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @param role     角色
     * @return 用户对象，如果验证失败则返回null
     */
    public User authenticate(String phoneOrEmail, String password, String role) {
        return userMapper.findByPhoneOrEmailAndPasswordAndRole(phoneOrEmail, password, role);
    }

    /**
     * 注册新用户
     *
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @param role     角色
     * @return 是否注册成功
     */
    public boolean register(String phoneOrEmail, String password, String role) {
        // 检查手机号是否已存在
        if (userMapper.countByPhone(phoneOrEmail) > 0) {
            return false;
        }
        
        // 检查邮箱是否已存在
        if (phoneOrEmail.contains("@") && userMapper.countByEmail(phoneOrEmail) > 0) {
            return false;
        }

        // 创建新用户
        User user = new User();
        user.setPassword(password);
        user.setRole(role);
        
        // 设置手机号或邮箱
        if (phoneOrEmail.contains("@")) {
            user.setEmail(phoneOrEmail);
        } else {
            user.setPhone(phoneOrEmail);
        }
        
        // 设置用户名为手机号或邮箱
        user.setUsername(phoneOrEmail);
        
        // 其他字段使用默认空值
        
        int result = userMapper.insert(user);
        return result > 0;
    }

    /**
     * 注册新用户（完整信息）
     *
     * @param phone 手机号
     * @param email 邮箱
     * @param password 密码
     * @param name 姓名
     * @param address 地址
     * @param role 角色
     * @return 是否注册成功
     */
    public User registerWithDetails(String phone, String email, String password, String name, String address, String role) {
        // 检查手机号是否已存在
        if (phone != null && !phone.isEmpty() && userMapper.countByPhone(phone) > 0) {
            return null;
        }
        
        // 检查邮箱是否已存在
        if (email != null && !email.isEmpty() && userMapper.countByEmail(email) > 0) {
            return null;
        }

        // 创建新用户
        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setAddress(address);
        user.setRole(role);
        user.setUsername(phone != null && !phone.isEmpty() ? phone : email);
        
        int result = userMapper.insert(user);
        if (result > 0) {
            return user;
        }
        return null;
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    /**
     * 更新用户信息
     *
     * @param id   用户ID
     * @param user 更新后的用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(Long id, User user) {
        user.setId(id); // 确保ID不变
        int result = userMapper.updateById(user);
        return result > 0;
    }

    /**
     * 修改用户密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            int result = userMapper.updateById(user);
            return result > 0;
        }
        return false;
    }

    /**
     * 管理员修改用户密码（无需旧密码）
     *
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public boolean adminChangePassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(newPassword);
            int result = userMapper.updateById(user);
            return result > 0;
        }
        return false;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(Long id) {
        int result = userMapper.deleteById(id);
        return result > 0;
    }
}