package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule; // 修改导入
import edu.mycc.xhd.pethospitalproject.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 查询排班信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param department 科室
     * @return 排班列表
     */
    @GetMapping
    public ResponseEntity<List<DoctorSchedule>> getSchedules(@RequestParam(required = false) String startDate,
                                                      @RequestParam(required = false) String endDate,
                                                      @RequestParam(required = false) String department) {
        if (startDate != null && endDate != null) {
            List<DoctorSchedule> schedules = scheduleService.getSchedulesByDateRangeAndDepartment(startDate, endDate, department);
            return ResponseEntity.ok(schedules);
        } else if (startDate != null) {
            // 如果只提供了一个日期，则查询该日期的排班
            List<DoctorSchedule> schedules = scheduleService.getSchedulesByDateAndDepartment(startDate, department);
            return ResponseEntity.ok(schedules);
        } else {
            // 返回错误信息
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 创建排班
     * @param schedule 排班信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody DoctorSchedule schedule) {
        // 这里可以添加创建排班的逻辑
        // 暂时返回成功
        return ResponseEntity.ok().body(Map.of("message", "排班创建成功"));
    }

    /**
     * 更新排班
     * @param id 排班ID
     * @param schedule 排班信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        // 这里可以添加更新排班的逻辑
        // 暂时返回成功
        return ResponseEntity.ok().body(Map.of("message", "排班更新成功"));
    }

    /**
     * 删除排班
     * @param id 排班ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        // 这里可以添加删除排班的逻辑
        // 暂时返回成功
        return ResponseEntity.ok().body(Map.of("message", "排班删除成功"));
    }

    /**
     * 将排班模板应用到指定周期
     * @param template 排班模板
     * @param department 科室
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 操作结果
     */
    @PostMapping("/apply-to-period")
    public ResponseEntity<?> applyScheduleToPeriod(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> template = (List<Map<String, Object>>) request.get("template");
            String department = (String) request.get("department");
            String startDate = (String) request.get("startDate");
            String endDate = (String) request.get("endDate");

            boolean success = scheduleService.saveScheduleTemplateToPeriod(template, department, startDate, endDate);

            if (success) {
                return ResponseEntity.ok().body(Map.of("message", "排班模板已成功应用到指定周期"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "应用排班模板失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "应用排班模板时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 获取指定科室的排班模板
     * @param department 科室
     * @return 排班模板
     */
    @GetMapping("/template")
    public ResponseEntity<List<Map<String, Object>>> getScheduleTemplate(@RequestParam String department) {
        List<Map<String, Object>> template = scheduleService.getWeeklyScheduleTemplate(department);
        return ResponseEntity.ok(template);
    }
}