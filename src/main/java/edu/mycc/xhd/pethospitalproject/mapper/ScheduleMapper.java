package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule; // 修改导入
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScheduleMapper extends BaseMapper<DoctorSchedule> { // 修改泛型类型
    
    /**
     * 根据日期范围和员工类型查询排班
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param employeeType 员工类型
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date BETWEEN #{startDate} AND #{endDate} AND 'doctor' = #{employeeType}")
    List<DoctorSchedule> findByDateRangeAndType(@Param("startDate") String startDate, 
                                         @Param("endDate") String endDate, 
                                         @Param("employeeType") String employeeType);
    
    /**
     * 根据日期范围、员工类型和科室查询排班
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param employeeType 员工类型
     * @param department 科室
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "AND 'doctor' = #{employeeType} AND department = #{department}")
    List<DoctorSchedule> findByDateRangeAndTypeAndDepartment(@Param("startDate") String startDate, 
                                                      @Param("endDate") String endDate, 
                                                      @Param("employeeType") String employeeType,
                                                      @Param("department") String department);
    
    /**
     * 根据医生ID和日期范围查询排班
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE doctor_id = #{doctorId} AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<DoctorSchedule> findByDoctorIdAndDateRange(@Param("doctorId") String doctorId,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate);
    
    /**
     * 根据日期和科室查询排班
     * @param date 日期
     * @param department 科室
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date = #{date} AND department = #{department}")
    List<DoctorSchedule> findByDateAndDepartment(@Param("date") String date,
                                         @Param("department") String department);
    
    /**
     * 根据日期查询所有排班
     * @param date 日期
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date = #{date}")
    List<DoctorSchedule> findByDate(@Param("date") String date);
}