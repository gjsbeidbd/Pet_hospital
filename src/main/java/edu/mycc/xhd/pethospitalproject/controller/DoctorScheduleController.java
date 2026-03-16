package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule;
import edu.mycc.xhd.pethospitalproject.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor-schedules")
@CrossOrigin(origins = "*")
public class DoctorScheduleController {
    
    @Autowired
    private DoctorScheduleService doctorScheduleService;
    
    /**
     * 获取医生排班列表
     */
    @GetMapping
    public Map<String, Object> getSchedules(
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam(required = false) String department
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDateRangeAndDepartment(startDate, endDate, department);
            result.put("code", "0");
            result.put("data", schedules);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 保存医生排班
     */
    @PostMapping
    public Map<String, Object> saveSchedule(@RequestBody DoctorSchedule schedule) {
        Map<String, Object> result = new HashMap<>();
        try {
            doctorScheduleService.save(schedule);
            result.put("code", "0");
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 批量保存医生排班
     */
    @PostMapping("/batch")
    public Map<String, Object> saveBatch(@RequestBody List<DoctorSchedule> schedules) {
        Map<String, Object> result = new HashMap<>();
        try {
            doctorScheduleService.saveBatch(schedules);
            result.put("code", "0");
            result.put("message", "批量保存成功");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除排班
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            doctorScheduleService.removeById(id);
            result.put("code", "0");
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
