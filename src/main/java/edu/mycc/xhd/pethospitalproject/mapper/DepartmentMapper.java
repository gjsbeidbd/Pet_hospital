package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 科室 Mapper 接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    
    /**
     * 统计各科室的医生人数
     * @return 科室代码和对应人数的列表
     */
    @Select("SELECT department, COUNT(*) as count FROM doctors WHERE status = 'active' GROUP BY department")
    List<Map<String, Object>> countDoctorsByDepartment();
}
