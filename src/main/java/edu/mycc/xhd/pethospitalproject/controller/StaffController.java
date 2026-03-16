package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import edu.mycc.xhd.pethospitalproject.entity.Receptionist;
import edu.mycc.xhd.pethospitalproject.service.DoctorService;
import edu.mycc.xhd.pethospitalproject.service.ReceptionistService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理 Controller - 统一管理医生、前台等员工信息
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
public class StaffController {
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private ReceptionistService receptionistService;
    
    /**
     * 获取所有员工列表（包括医生和前台）
     */
    @GetMapping
    public List<Map<String, Object>> getAllStaff() {
        List<Map<String, Object>> staffList = new ArrayList<>();
        
        // 获取所有医生
        List<Doctor> doctors = doctorService.list();
        for (Doctor doctor : doctors) {
            Map<String, Object> staffMap = new HashMap<>();
            staffMap.put("id", doctor.getId());
            staffMap.put("employeeId", doctor.getEmployeeId());
            staffMap.put("name", doctor.getName());
            staffMap.put("birthday", doctor.getBirthday());
            staffMap.put("idCard", doctor.getIdCard());
            staffMap.put("address", doctor.getAddress());
            staffMap.put("role", "医生");
            staffMap.put("department", doctor.getDepartment());
            staffMap.put("phone", doctor.getPhone());
            staffMap.put("email", doctor.getEmail());
            staffMap.put("hireDate", doctor.getHireDate());
            staffMap.put("position", doctor.getPosition());
            staffMap.put("status", "active".equals(doctor.getStatus()));
            staffList.add(staffMap);
        }
        
        // 获取所有前台
        List<Receptionist> receptionists = receptionistService.list();
        for (Receptionist receptionist : receptionists) {
            Map<String, Object> staffMap = new HashMap<>();
            staffMap.put("id", receptionist.getId());
            staffMap.put("employeeId", receptionist.getEmployeeId());
            staffMap.put("name", receptionist.getName());
            staffMap.put("birthday", receptionist.getBirthday());
            staffMap.put("idCard", receptionist.getIdCard());
            staffMap.put("address", receptionist.getAddress());
            staffMap.put("role", "前台");
            staffMap.put("department", receptionist.getDepartment()); // 使用数据库中的科室
            staffMap.put("phone", receptionist.getPhone());
            staffMap.put("email", receptionist.getEmail());
            staffMap.put("hireDate", receptionist.getHireDate());
            staffMap.put("position", receptionist.getPosition());
            staffMap.put("status", "active".equals(receptionist.getStatus()));
            staffList.add(staffMap);
        }
        
        return staffList;
    }
    
    /**
     * 根据 ID 获取员工信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getStaffById(@PathVariable Long id) {
        // 先查医生
        Doctor doctor = doctorService.getById(id);
        if (doctor != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", doctor.getId());
            result.put("employeeId", doctor.getEmployeeId());
            result.put("name", doctor.getName());
            result.put("birthday", doctor.getBirthday());
            result.put("idCard", doctor.getIdCard());
            result.put("address", doctor.getAddress());
            result.put("role", "医生");
            result.put("department", doctor.getDepartment());
            result.put("phone", doctor.getPhone());
            result.put("email", doctor.getEmail());
            result.put("hireDate", doctor.getHireDate());
            result.put("position", doctor.getPosition());
            result.put("status", "active".equals(doctor.getStatus()));
            return result;
        }
        
        // 再查前台
        Receptionist receptionist = receptionistService.getById(id);
        if (receptionist != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", receptionist.getId());
            result.put("employeeId", receptionist.getEmployeeId());
            result.put("name", receptionist.getName());
            result.put("birthday", receptionist.getBirthday());
            result.put("idCard", receptionist.getIdCard());
            result.put("address", receptionist.getAddress());
            result.put("role", "前台");
            result.put("department", "护理部");
            result.put("phone", receptionist.getPhone());
            result.put("email", receptionist.getEmail());
            result.put("hireDate", receptionist.getHireDate());
            result.put("position", receptionist.getPosition());
            result.put("status", "active".equals(receptionist.getStatus()));
            return result;
        }
        
        return null;
    }
    
    /**
     * 获取前台人员的科室列表（从 receptionist 表中提取）
     */
    @GetMapping("/receptionist-departments")
    public List<Map<String, Object>> getReceptionistDepartments() {
        List<Map<String, Object>> departments = new ArrayList<>();
        
        // 获取所有前台
        List<Receptionist> receptionists = receptionistService.list();
        
        // 提取不重复的科室
        java.util.Set<String> deptSet = new java.util.HashSet<>();
        for (Receptionist receptionist : receptionists) {
            String dept = receptionist.getDepartment();
            if (dept != null && !dept.isEmpty()) {
                deptSet.add(dept);
            }
        }
        
        // 转换为返回格式
        for (String dept : deptSet) {
            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("name", dept);
            departments.add(deptMap);
        }
        
        return departments;
    }
    
    /**
     * 根据科室获取医生列表
     */
    @GetMapping("/doctors")
    public List<Map<String, Object>> getDoctorsByDepartment(@RequestParam(required = false) String department) {
        List<Map<String, Object>> doctors = new ArrayList<>();
        
        // 获取所有医生
        List<Doctor> doctorList = doctorService.list();
        
        for (Doctor doctor : doctorList) {
            // 如果指定了科室名称，则过滤
            if (department != null && !department.isEmpty()) {
                if (!department.equals(doctor.getDepartment())) {
                    continue;
                }
            }
            
            Map<String, Object> doctorMap = new HashMap<>();
            doctorMap.put("id", doctor.getId());
            doctorMap.put("employeeId", doctor.getEmployeeId());
            doctorMap.put("name", doctor.getName());
            doctorMap.put("department", doctor.getDepartment());
            doctorMap.put("position", doctor.getPosition());
            doctorMap.put("phone", doctor.getPhone());
            doctorMap.put("email", doctor.getEmail());
            doctors.add(doctorMap);
        }
        
        return doctors;
    }
    
    /**
     * 获取所有护士列表
     */
    @GetMapping("/receptionists")
    public List<Map<String, Object>> getAllReceptionists() {
        List<Map<String, Object>> receptionists = new ArrayList<>();
        
        List<Receptionist> receptionistList = receptionistService.list();
        
        for (Receptionist receptionist : receptionistList) {
            Map<String, Object> receptionistMap = new HashMap<>();
            receptionistMap.put("id", receptionist.getId());
            receptionistMap.put("employeeId", receptionist.getEmployeeId());
            receptionistMap.put("name", receptionist.getName());
            receptionistMap.put("department", receptionist.getDepartment());
            receptionistMap.put("position", receptionist.getPosition());
            receptionistMap.put("phone", receptionist.getPhone());
            receptionistMap.put("email", receptionist.getEmail());
            receptionists.add(receptionistMap);
        }
        
        return receptionists;
    }
    
    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateStaff(@PathVariable Long id, @RequestBody Map<String, Object> staffData) {
        System.out.println("=== 接收到更新员工请求 ===");
        System.out.println("员工 ID: " + id);
        System.out.println("提交的数据：" + staffData);
        
        String role = (String) staffData.get("role");
        System.out.println("职位类型：" + role);
        
        if ("医生".equals(role)) {
            Doctor doctor = doctorService.getById(id);
            if (doctor != null) {
                System.out.println("更新前 - 原职称：" + doctor.getPosition());
                System.out.println("更新后 - 新职称：" + staffData.get("position"));
                
                doctor.setName((String) staffData.get("name"));
                doctor.setBirthday((String) staffData.get("birthday"));
                doctor.setIdCard((String) staffData.get("idCard"));
                doctor.setAddress((String) staffData.get("address"));
                doctor.setDepartment((String) staffData.get("department"));
                doctor.setPosition((String) staffData.get("position")); // 更新职称
                doctor.setPhone((String) staffData.get("phone"));
                doctor.setEmail((String) staffData.get("email"));
                
                Boolean status = (Boolean) staffData.get("status");
                doctor.setStatus(status ? "active" : "inactive");
                
                System.out.println("准备更新医生信息...");
                boolean updated = doctorService.updateById(doctor);
                System.out.println("更新结果：" + updated);
                System.out.println("更新后医生 ID: " + doctor.getId());
                System.out.println("更新后医生职称：" + doctor.getPosition());
                
                Map<String, Object> result = new HashMap<>();
                result.put("message", "医生信息更新成功");
                result.put("data", doctor);
                return result;
            } else {
                System.out.println("医生不存在，ID: " + id);
            }
        } else if ("护士".equals(role) || "前台".equals(role)) {
            Receptionist receptionist = receptionistService.getById(id);
            if (receptionist != null) {
                receptionist.setName((String) staffData.get("name"));
                receptionist.setBirthday((String) staffData.get("birthday"));
                receptionist.setIdCard((String) staffData.get("idCard"));
                receptionist.setAddress((String) staffData.get("address"));
                receptionist.setDepartment((String) staffData.get("department"));
                receptionist.setPosition((String) staffData.get("position"));
                receptionist.setPhone((String) staffData.get("phone"));
                receptionist.setEmail((String) staffData.get("email"));
                
                Boolean status = (Boolean) staffData.get("status");
                receptionist.setStatus(status ? "active" : "inactive");
                
                receptionistService.updateById(receptionist);
                
                Map<String, Object> result = new HashMap<>();
                result.put("message", "护士/前台信息更新成功");
                result.put("data", receptionist);
                return result;
            }
        }
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "员工不存在或角色不匹配");
        return error;
    }
    
    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteStaff(@PathVariable Long id) {
        // 尝试删除医生
        Doctor doctor = doctorService.getById(id);
        if (doctor != null) {
            doctorService.removeById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "医生删除成功");
            return result;
        }
        
        // 尝试删除前台
        Receptionist receptionist = receptionistService.getById(id);
        if (receptionist != null) {
            receptionistService.removeById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "前台删除成功");
            return result;
        }
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "员工不存在");
        return error;
    }
    
    /**
     * 新增员工（医生或前台）
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addStaff(@RequestBody Map<String, Object> staffData) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("=== 接收到新增员工请求 ===");
        System.out.println("提交的数据：" + staffData);
        try {
            String role = (String) staffData.get("role");
            String password = (String) staffData.getOrDefault("password", "123456");
            
            System.out.println("职位类型：" + role);
            System.out.println("科室：" + staffData.get("department"));
            
            if ("医生".equals(role)) {
                Doctor doctor = new Doctor();
                doctor.setEmployeeId((String) staffData.get("employeeId"));
                doctor.setName((String) staffData.get("name"));
                doctor.setBirthday((String) staffData.get("birthday"));
                doctor.setIdCard((String) staffData.get("idCard"));
                doctor.setAddress((String) staffData.get("address"));
                doctor.setDepartment((String) staffData.get("department"));
                doctor.setPosition((String) staffData.get("position"));
                doctor.setPhone((String) staffData.get("phone"));
                doctor.setEmail((String) staffData.get("email"));
                doctor.setHireDate((String) staffData.get("hireDate"));
                doctor.setPassword(password);
                doctor.setStatus("active");
                
                System.out.println("保存医生信息...");
                boolean saved = doctorService.save(doctor);
                System.out.println("保存结果：" + saved);
                System.out.println("生成的 ID: " + doctor.getId());
                
                result.put("message", "医生添加成功");
                result.put("data", doctor);
            } else if ("护士".equals(role) || "前台".equals(role)) {
                Receptionist receptionist = new Receptionist();
                receptionist.setEmployeeId((String) staffData.get("employeeId"));
                receptionist.setName((String) staffData.get("name"));
                receptionist.setBirthday((String) staffData.get("birthday"));
                receptionist.setIdCard((String) staffData.get("idCard"));
                receptionist.setAddress((String) staffData.get("address"));
                receptionist.setDepartment((String) staffData.get("department"));
                receptionist.setPosition((String) staffData.get("position"));
                receptionist.setPhone((String) staffData.get("phone"));
                receptionist.setEmail((String) staffData.get("email"));
                receptionist.setHireDate((String) staffData.get("hireDate"));
                receptionist.setPassword(password);
                receptionist.setStatus("active");
                
                System.out.println("保存护士/前台信息...");
                boolean saved = receptionistService.save(receptionist);
                System.out.println("保存结果：" + saved);
                System.out.println("生成的 ID: " + receptionist.getId());
                
                result.put("message", "护士/前台添加成功");
                result.put("data", receptionist);
            } else {
                result.put("code", "500");
                result.put("message", "不支持的职位类型");
                return result;
            }
            
            result.put("code", "0");
        } catch (Exception e) {
            System.err.println("添加失败：" + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("message", "添加失败：" + e.getMessage());
        }
        return result;
    }
}
