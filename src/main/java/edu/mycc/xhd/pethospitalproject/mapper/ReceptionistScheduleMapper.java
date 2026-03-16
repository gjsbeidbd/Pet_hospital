package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.ReceptionistSchedule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReceptionistScheduleMapper extends BaseMapper<ReceptionistSchedule> {
    
    /**
     * 根据日期范围和科室查询前台排班
     */
    @Select("<script>" +
            "SELECT * FROM receptionist_schedules " +
            "WHERE schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='department != null and department != \"\"'>" +
            "AND department = #{department}" +
            "</if>" +
            "</script>")
    List<ReceptionistSchedule> findByDateRangeAndDepartment(
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate, 
        @Param("department") String department);
    
    /**
     * 根据前台 ID 和日期范围查询排班
     */
    @Select("SELECT * FROM receptionist_schedules WHERE receptionist_id = #{receptionistId} AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<ReceptionistSchedule> findByReceptionistIdAndDateRange(
        @Param("receptionistId") Long receptionistId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);
}
