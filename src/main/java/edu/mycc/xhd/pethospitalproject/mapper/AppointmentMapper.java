package edu.mycc.xhd.pethospitalproject.mapper;

import edu.mycc.xhd.pethospitalproject.entity.Appointment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    
    @Select("SELECT * FROM appointments WHERE user_id = #{userId} ORDER BY appointment_date DESC, appointment_time DESC")
    @Results({
        @Result(property = "id", column = "id", jdbcType = JdbcType.BIGINT),
        @Result(property = "userId", column = "user_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "petId", column = "pet_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "doctorId", column = "doctor_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "appointmentDate", column = "appointment_date", jdbcType = JdbcType.DATE),
        @Result(property = "appointmentTime", column = "appointment_time", jdbcType = JdbcType.TIME),
        @Result(property = "status", column = "status", jdbcType = JdbcType.VARCHAR),
        @Result(property = "reason", column = "reason", jdbcType = JdbcType.LONGVARCHAR),
        @Result(property = "notes", column = "notes", jdbcType = JdbcType.LONGVARCHAR),
        @Result(property = "department", column = "department", jdbcType = JdbcType.VARCHAR),
        @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
        @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
    })
    List<Appointment> selectAppointmentsByUserId(Long userId);
    
    @Select("SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC")
    @Results({
        @Result(property = "id", column = "id", jdbcType = JdbcType.BIGINT),
        @Result(property = "userId", column = "user_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "petId", column = "pet_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "doctorId", column = "doctor_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "appointmentDate", column = "appointment_date", jdbcType = JdbcType.DATE),
        @Result(property = "appointmentTime", column = "appointment_time", jdbcType = JdbcType.TIME),
        @Result(property = "status", column = "status", jdbcType = JdbcType.VARCHAR),
        @Result(property = "reason", column = "reason", jdbcType = JdbcType.LONGVARCHAR),
        @Result(property = "notes", column = "notes", jdbcType = JdbcType.LONGVARCHAR),
        @Result(property = "department", column = "department", jdbcType = JdbcType.VARCHAR),
        @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
        @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
    })
    List<Appointment> selectAllAppointments();

    @Select("SELECT * FROM appointments WHERE doctor_id = #{doctorId} AND appointment_date = CURRENT_DATE ORDER BY appointment_time ASC")
    @Results({
        @Result(property = "id", column = "id", jdbcType = JdbcType.BIGINT, id = true),
        @Result(property = "petId", column = "pet_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "doctorId", column = "doctor_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "userId", column = "user_id", jdbcType = JdbcType.BIGINT),
        @Result(property = "appointmentDate", column = "appointment_date", jdbcType = JdbcType.DATE),
        @Result(property = "appointmentTime", column = "appointment_time", jdbcType = JdbcType.VARCHAR),
        @Result(property = "status", column = "status", jdbcType = JdbcType.VARCHAR),
        @Result(property = "reason", column = "reason", jdbcType = JdbcType.VARCHAR),
        @Result(property = "department", column = "department", jdbcType = JdbcType.VARCHAR),
        @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
        @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
    })
    List<Appointment> selectByDoctorId(@Param("doctorId") Long doctorId);

    @Select("SELECT department, COUNT(*) as count FROM appointments " +
            "WHERE YEAR(appointment_date) = YEAR(CURRENT_DATE) AND MONTH(appointment_date) = MONTH(CURRENT_DATE) " +
            "GROUP BY department ORDER BY count DESC")
    List<Map<String, Object>> selectAppointmentCountByDepartment();

    @Select("SELECT COUNT(*) FROM appointments " +
            "WHERE department = #{department} AND status = 'completed' " +
            "AND appointment_date = CURRENT_DATE")
    int countTodayCompletedByDepartment(@Param("department") String department);
}
