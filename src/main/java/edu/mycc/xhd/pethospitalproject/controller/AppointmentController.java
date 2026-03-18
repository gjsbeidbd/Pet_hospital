package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Appointment;
import edu.mycc.xhd.pethospitalproject.entity.Doctor;
import edu.mycc.xhd.pethospitalproject.entity.Pet;
import edu.mycc.xhd.pethospitalproject.entity.User;
import edu.mycc.xhd.pethospitalproject.service.AppointmentService;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorMapper;
import edu.mycc.xhd.pethospitalproject.mapper.PetMapper;
import edu.mycc.xhd.pethospitalproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5180")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserMapper userMapper;

    // 获取用户预约列表
    @GetMapping
    public ResponseEntity<List<Appointment>> getUserAppointments(@RequestParam Long userId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByUserId(userId);
            // 填充关联数据
            appointments.forEach(app -> {
                if (app.getDoctorId() != null) {
                    Doctor doctor = doctorMapper.selectById(app.getDoctorId());
                    if (doctor != null) {
                        app.setDoctorName(doctor.getName());
                    }
                }
                if (app.getPetId() != null) {
                    Pet pet = petMapper.selectById(app.getPetId());
                    if (pet != null) {
                        app.setPetName(pet.getName());
                        app.setPetSpecies(pet.getSpecies());
                        app.setPetBreed(pet.getBreed());
                    }
                }
                if (app.getUserId() != null) {
                    User user = userMapper.selectById(app.getUserId());
                    if (user != null) {
                        app.setUserName(user.getName());
                    }
                }
            });
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 创建新预约
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        System.out.println("接收到预约请求: " + appointment);
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            System.out.println("预约创建成功: " + createdAppointment);
            return ResponseEntity.ok(createdAppointment);
        } catch (Exception e) {
            System.out.println("预约创建失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 取消预约
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id) {
        Appointment cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }

    // 获取所有预约（用于前台/医生查看）
    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        // 填充关联数据
        appointments.forEach(app -> {
            if (app.getDoctorId() != null) {
                Doctor doctor = doctorMapper.selectById(app.getDoctorId());
                if (doctor != null) {
                    app.setDoctorName(doctor.getName());
                }
            }
            if (app.getPetId() != null) {
                Pet pet = petMapper.selectById(app.getPetId());
                if (pet != null) {
                    app.setPetName(pet.getName());
                    app.setPetSpecies(pet.getSpecies());
                    app.setPetBreed(pet.getBreed());
                }
            }
            if (app.getUserId() != null) {
                User user = userMapper.selectById(app.getUserId());
                if (user != null) {
                    app.setUserName(user.getName());
                }
            }
        });
        return ResponseEntity.ok(appointments);
    }

    // 根据科室获取所有预约（用于前台）
    @GetMapping("/all-by-department")
    public ResponseEntity<List<Appointment>> getAllAppointmentsByDepartment(@RequestParam String department) {
        System.out.println("收到科室请求: " + department);
        List<Appointment> appointments = appointmentService.getAllAppointmentsByDepartment(department);
        System.out.println("查询到 " + appointments.size() + " 个预约");
        // 填充关联数据
        appointments.forEach(app -> {
            if (app.getDoctorId() != null) {
                Doctor doctor = doctorMapper.selectById(app.getDoctorId());
                if (doctor != null) {
                    app.setDoctorName(doctor.getName());
                }
            }
            if (app.getPetId() != null) {
                Pet pet = petMapper.selectById(app.getPetId());
                if (pet != null) {
                    app.setPetName(pet.getName());
                    app.setPetSpecies(pet.getSpecies());
                    app.setPetBreed(pet.getBreed());
                }
            }
            if (app.getUserId() != null) {
                User user = userMapper.selectById(app.getUserId());
                if (user != null) {
                    app.setUserName(user.getName());
                }
            }
        });
        System.out.println("返回数据: " + appointments);
        return ResponseEntity.ok(appointments);
    }

    // 获取预约详情
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    // 更新预约状态
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
    }

    // 开始就诊
    @PutMapping("/{id}/start-consultation")
    public ResponseEntity<Appointment> startConsultation(@PathVariable Long id, @RequestParam Long doctorId) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        
        // 验证医生权限：只有预约的医生才能开始接诊
        if (appointment == null) {
            return ResponseEntity.badRequest().build();
        }
        if (appointment.getDoctorId() != null && !appointment.getDoctorId().equals(doctorId)) {
            return ResponseEntity.status(403).build();
        }
        
        Appointment updatedAppointment = appointmentService.startConsultation(id);
        return ResponseEntity.ok(updatedAppointment);
    }

    // 完成就诊
    @PutMapping("/{id}/finish-consultation")
    public ResponseEntity<Appointment> finishConsultation(@PathVariable Long id, @RequestParam Long doctorId) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        
        // 验证医生权限：只有预约的医生才能完成诊疗
        if (appointment == null) {
            return ResponseEntity.badRequest().build();
        }
        if (appointment.getDoctorId() != null && !appointment.getDoctorId().equals(doctorId)) {
            return ResponseEntity.status(403).build();
        }
        
        Appointment updatedAppointment = appointmentService.finishConsultation(id);
        return ResponseEntity.ok(updatedAppointment);
    }

    // 前台取号
    @PutMapping("/{id}/take-number")
    public ResponseEntity<Appointment> takeNumber(@PathVariable Long id) {
        Appointment takenAppointment = appointmentService.takeNumber(id);
        return ResponseEntity.ok(takenAppointment);
    }
}