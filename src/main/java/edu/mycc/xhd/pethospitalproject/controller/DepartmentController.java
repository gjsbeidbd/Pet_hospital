package edu.mycc.xhd.pethospitalproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.mycc.xhd.pethospitalproject.entity.Department;
import edu.mycc.xhd.pethospitalproject.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 科室管理 Controller
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    /**
     * 获取各科室医生人数统计
     */
    @GetMapping("/stats")
    public List<Map<String, Object>> getDepartmentStats() {
        return departmentService.countDoctorsByDepartment();
    }
    
    /**
     * 获取所有科室
     */
    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.list(new LambdaQueryWrapper<Department>().orderByDesc(Department::getId));
    }
    
    /**
     * 根据 ID 获取科室
     */
    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable Long id) {
        return departmentService.getById(id);
    }
    
    /**
     * 新增科室
     */
    @PostMapping
    public boolean save(@RequestBody Department department) {
        // 设置默认值
        if (department.getDoctorCount() == null) {
            department.setDoctorCount(0);
        }
        if (department.getIsActive() == null) {
            department.setIsActive(true);
        }
        return departmentService.save(department);
    }
    
    /**
     * 更新科室
     */
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        return departmentService.updateById(department);
    }
    
    /**
     * 删除科室（同时删除相关排班数据）
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return departmentService.deleteDepartmentWithSchedules(id);
    }
}
