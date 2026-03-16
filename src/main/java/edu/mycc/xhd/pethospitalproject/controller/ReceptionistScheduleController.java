package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.ReceptionistSchedule;
import edu.mycc.xhd.pethospitalproject.service.ReceptionistScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receptionist-schedules")
public class ReceptionistScheduleController {
    
    @Autowired
    private ReceptionistScheduleService receptionistScheduleService;
    
    /**
     * 根据日期范围获取所有科室护士排班
     */
    @GetMapping("/date-range")
    public Map<String, Object> getSchedulesByDateRange(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ReceptionistSchedule> schedules = receptionistScheduleService.getSchedulesByDateRangeAndDepartment(startDate, endDate, null);
            result.put("code", "0");
            result.put("data", schedules);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取前台排班列表
     */
    @GetMapping
    public Map<String, Object> getSchedules(
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam(required = false) String department
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ReceptionistSchedule> schedules = receptionistScheduleService.getSchedulesByDateRangeAndDepartment(startDate, endDate, department);
            result.put("code", "0");
            result.put("data", schedules);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 保存前台排班
     */
    @PostMapping
    public Map<String, Object> saveSchedule(@RequestBody ReceptionistSchedule schedule) {
        Map<String, Object> result = new HashMap<>();
        try {
            receptionistScheduleService.save(schedule);
            result.put("code", "0");
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 批量保存前台排班（覆盖模式：先删除日期范围内的排班再保存）
     */
    @PostMapping("/batch")
    public Map<String, Object> saveBatch(@RequestBody List<ReceptionistSchedule> schedules) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (schedules != null && !schedules.isEmpty()) {
                String startDate = schedules.get(0).getScheduleDate();
                String endDate = schedules.get(schedules.size() - 1).getScheduleDate();
                receptionistScheduleService.deleteByDateRange(startDate, endDate);
            }
            receptionistScheduleService.saveBatch(schedules);
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
     * 删除排班
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            receptionistScheduleService.removeById(id);
            result.put("code", "0");
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
