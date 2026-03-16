package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.Department;
import edu.mycc.xhd.pethospitalproject.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 科室 Service
 */
@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    /**
     * 统计各科室的医生人数
     * @return 科室代码和对应人数的列表
     */
    public List<Map<String, Object>> countDoctorsByDepartment() {
        return departmentMapper.countDoctorsByDepartment();
    }
}
