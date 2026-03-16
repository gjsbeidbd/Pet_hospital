package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.Department;
import edu.mycc.xhd.pethospitalproject.mapper.DepartmentMapper;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorMapper;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorScheduleMapper;
import edu.mycc.xhd.pethospitalproject.mapper.ReceptionistMapper;
import edu.mycc.xhd.pethospitalproject.mapper.ReceptionistScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;
    
    @Autowired
    private ReceptionistMapper receptionistMapper;
    
    @Autowired
    private ReceptionistScheduleMapper receptionistScheduleMapper;
    
    public List<Map<String, Object>> countDoctorsByDepartment() {
        return departmentMapper.countDoctorsByDepartment();
    }
    
    @Transactional
    public boolean deleteDepartmentWithSchedules(Long id) {
        Department department = this.getById(id);
        if (department == null) {
            return false;
        }
        
        String deptName = department.getName();
        
        doctorScheduleMapper.deleteByDepartment(deptName);
        
        receptionistScheduleMapper.deleteByDepartment(deptName);
        
        doctorMapper.deleteByDepartment(deptName);
        
        receptionistMapper.deleteByDepartment(deptName);
        
        return this.removeById(id);
    }
}
