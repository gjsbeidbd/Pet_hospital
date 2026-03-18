package edu.mycc.xhd.pethospitalproject.mapper;

import edu.mycc.xhd.pethospitalproject.entity.Appointment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

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
}
