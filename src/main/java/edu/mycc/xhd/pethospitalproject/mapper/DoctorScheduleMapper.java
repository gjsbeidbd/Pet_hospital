package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {
    
    /**
     * 根据日期范围和科室查询医生排班
     */
    @Select("<script>" +
            "SELECT * FROM doctor_schedules " +
            "WHERE schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='department != null and department != \"\"'>" +
            "AND department = #{department}" +
            "</if>" +
            "</script>")
    List<DoctorSchedule> findByDateRangeAndDepartment(
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate, 
        @Param("department") String department);
    
    /**
     * 根据医生 ID 和日期范围查询排班
     */
    @Select("SELECT * FROM doctor_schedules WHERE doctor_id = #{doctorId} AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<DoctorSchedule> findByDoctorIdAndDateRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);
    
    /**
     * 根据日期和科室查询排班
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date = #{date} AND department = #{department}")
    List<DoctorSchedule> findByDateAndDepartment(
        @Param("date") String date,
        @Param("department") String department);
    
    /**
     * 根据日期范围删除排班
     */
    @Delete("DELETE FROM doctor_schedules WHERE schedule_date BETWEEN #{startDate} AND #{endDate}")
    void deleteByDateRange(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);
    
    /**
     * 根据科室删除排班
     */
    @Delete("DELETE FROM doctor_schedules WHERE department = #{department}")
    void deleteByDepartment(@Param("department") String department);
}
