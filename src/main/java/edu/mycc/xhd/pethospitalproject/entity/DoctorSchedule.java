package edu.mycc.xhd.pethospitalproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("doctor_schedules")
public class DoctorSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long doctorId;
    private String department;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private String shiftType;
    @TableField("is_emergency_24h")
    private Boolean emergency24h;
    private String emergencyPeriod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DoctorSchedule() {}

    public DoctorSchedule(Long id, Long doctorId, String department, String scheduleDate, String startTime,
                         String endTime, String shiftType, Boolean emergency24h, String emergencyPeriod,
                         String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.department = department;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftType = shiftType;
        this.emergency24h = emergency24h;
        this.emergencyPeriod = emergencyPeriod;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public Boolean getEmergency24h() {
        return emergency24h;
    }

    public void setEmergency24h(Boolean emergency24h) {
        this.emergency24h = emergency24h;
    }

    public String getEmergencyPeriod() {
        return emergencyPeriod;
    }

    public void setEmergencyPeriod(String emergencyPeriod) {
        this.emergencyPeriod = emergencyPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
