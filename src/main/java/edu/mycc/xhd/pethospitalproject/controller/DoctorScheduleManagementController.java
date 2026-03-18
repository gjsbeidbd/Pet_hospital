package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule;
import edu.mycc.xhd.pethospitalproject.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生排班管理 Controller
 */
@RestController
@RequestMapping("/api/doctor-schedules")
public class DoctorScheduleManagementController {
    
    @Autowired
    private DoctorScheduleService doctorScheduleService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根据日期范围获取所有科室排班
     */
    @GetMapping("/date-range")
    public Map<String, Object> getSchedulesByDateRange(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDateRangeAndDepartment(startDate, endDate, null);
            result.put("code", "0");
            result.put("data", schedules);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据科室和月份获取排班列表
     */
    @GetMapping("/department/month")
    public Map<String, Object> getSchedulesByDepartmentAndMonth(
        @RequestParam String department,
        @RequestParam String startDate,
        @RequestParam String endDate
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
     * 保存排班（单个）
     */
    @PostMapping
    public Map<String, Object> saveSchedule(@RequestBody DoctorSchedule schedule) {
        Map<String, Object> result = new HashMap<>();
        try {
            doctorScheduleService.save(schedule);
            result.put("code", "0");
            result.put("message", "保存成功");
            result.put("data", schedule);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 批量保存排班（覆盖模式：先删除日期范围内的排班再保存）
     */
    @PostMapping("/batch")
    public Map<String, Object> saveBatch(@RequestBody List<DoctorSchedule> schedules) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (schedules != null && !schedules.isEmpty()) {
                String startDate = schedules.get(0).getScheduleDate();
                String endDate = schedules.get(schedules.size() - 1).getScheduleDate();
                doctorScheduleService.deleteByDateRange(startDate, endDate);
            }
            doctorScheduleService.saveBatch(schedules);
            result.put("code", "0");
            result.put("message", "批量保存成功");
            result.put("count", schedules.size());
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据医生ID和日期范围获取排班
     */
    @GetMapping("/doctor/{doctorId}")
    public Map<String, Object> getSchedulesByDoctorIdAndDateRange(
        @PathVariable Long doctorId,
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDoctorIdAndDateRange(doctorId, startDate, endDate);
            result.put("code", "0");
            result.put("data", schedules);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除日期范围内的排班
     */
    @DeleteMapping("/date-range")
    public Map<String, Object> deleteByDateRange(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            doctorScheduleService.deleteByDateRange(startDate, endDate);
            result.put("code", "0");
            result.put("message", "删除成功");
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
    
    /**
     * 根据日期和科室获取值班医生列表（返回完整医生信息）
     */
    @GetMapping("/on-duty/doctors")
    public Map<String, Object> getOnDutyDoctorsByDateAndDepartment(
        @RequestParam String date,
        @RequestParam String department
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 第一步：从排班表中查询指定日期和科室的排班记录
            List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDateRangeAndDepartment(date, date, department);
            
            if (schedules == null || schedules.isEmpty()) {
                result.put("code", "0");
                result.put("data", new java.util.ArrayList<>());
                return result;
            }
            
            // 第二步：提取不重复的医生 ID
            java.util.Set<Long> doctorIdsSet = new java.util.HashSet<>();
            for (DoctorSchedule schedule : schedules) {
                if (schedule.getDoctorId() != null) {
                    doctorIdsSet.add(schedule.getDoctorId());
                }
            }
            
            // 第三步：根据医生 ID 列表查询医生详细信息
            if (doctorIdsSet.isEmpty()) {
                result.put("code", "0");
                result.put("data", new java.util.ArrayList<>());
            } else {
                // 构建 IN 查询，返回与 getDoctorsByDepartment 相同的字段格式
                String idsStr = String.join(",", doctorIdsSet.stream().map(String::valueOf).toArray(String[]::new));
                String sql = "SELECT id, name, position, phone, email FROM doctors WHERE id IN (" + idsStr + ")";
                List<Map<String, Object>> doctors = jdbcTemplate.queryForList(sql);
                
                result.put("code", "0");
                result.put("data", doctors);
            }
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
