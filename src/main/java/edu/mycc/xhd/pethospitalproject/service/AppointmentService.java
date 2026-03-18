package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    // 根据用户ID获取预约列表
    List<Appointment> getAppointmentsByUserId(Long userId);

    // 创建新预约
    Appointment createAppointment(Appointment appointment);

    // 取号（前台操作）
    Appointment takeNumber(Long appointmentId);

    // 开始就诊（医生操作）
    Appointment startConsultation(Long appointmentId);

    // 完成就诊（医生操作）
    Appointment finishConsultation(Long appointmentId);

    // 取消预约
    Appointment cancelAppointment(Long appointmentId);

    // 根据ID获取预约详情
    Appointment getAppointmentById(Long appointmentId);

    // 获取所有预约
    List<Appointment> getAllAppointments();

    // 根据科室获取所有预约（用于前台）
    List<Appointment> getAllAppointmentsByDepartment(String department);

    // 更新预约状态
    Appointment updateAppointmentStatus(Long appointmentId, String status);
}