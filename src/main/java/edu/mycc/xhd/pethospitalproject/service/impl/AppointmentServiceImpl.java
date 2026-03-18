package edu.mycc.xhd.pethospitalproject.service.impl;

import edu.mycc.xhd.pethospitalproject.entity.Appointment;
import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.entity.Pet;
import edu.mycc.xhd.pethospitalproject.mapper.AppointmentMapper;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorMapper;
import edu.mycc.xhd.pethospitalproject.mapper.UserMapper;
import edu.mycc.xhd.pethospitalproject.mapper.PetMapper;
import edu.mycc.xhd.pethospitalproject.service.AppointmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PetMapper petMapper;

    @Override
    public List<Appointment> getAppointmentsByUserId(Long userId) {
        return appointmentMapper.selectAppointmentsByUserId(userId);
    }

    @Override
    public Appointment createAppointment(Appointment appointment) {
        // 如果没有指定科室，从医生信息中获取
        if (appointment.getDepartment() == null && appointment.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor != null) {
                appointment.setDepartment(doctor.getDepartment());
            }
        }
        
        // 设置默认状态为pending
        appointment.setStatus("pending");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        baseMapper.insert(appointment);
        return appointment;
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("cancelled");
            appointment.setUpdatedAt(LocalDateTime.now());
            baseMapper.updateById(appointment);
        }
        return appointment;
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentMapper.selectById(appointmentId);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentMapper.selectAllAppointments();
    }

    @Override
    public List<Appointment> getAllAppointmentsByDepartment(String department) {
        if (department == null || department.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("department", department);
        wrapper.orderByDesc("appointment_date");
        wrapper.orderByDesc("appointment_time");
        List<Appointment> appointments = appointmentMapper.selectList(wrapper);
        System.out.println("根据科室 [" + department + "] 查询到 " + appointments.size() + " 个预约");
        return appointments;
    }

    @Override
    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (appointment != null) {
            appointment.setStatus(status);
            appointment.setUpdatedAt(LocalDateTime.now());
            baseMapper.updateById(appointment);
        }
        return appointment;
    }

    @Override
    public Appointment takeNumber(Long appointmentId) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("waiting");
            appointment.setUpdatedAt(LocalDateTime.now());
            baseMapper.updateById(appointment);
        }
        return appointment;
    }

    @Override
    public Appointment startConsultation(Long appointmentId) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("in_progress");
            appointment.setUpdatedAt(LocalDateTime.now());
            baseMapper.updateById(appointment);
        }
        return appointment;
    }

    @Override
    public Appointment finishConsultation(Long appointmentId) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (appointment != null) {
            appointment.setStatus("completed");
            appointment.setUpdatedAt(LocalDateTime.now());
            baseMapper.updateById(appointment);
        }
        return appointment;
    }
}