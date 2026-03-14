package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule; // 修改导入
import edu.mycc.xhd.pethospitalproject.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleMapper scheduleMapper;
    
    /**
     * 保存排班模板到指定周期
     * @param template 排班模板
     * @param department 科室
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 操作结果
     */
    public boolean saveScheduleTemplateToPeriod(List<Map<String, Object>> template, String department, 
                                               String startDate, String endDate) {
        try {
            // 解析日期
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // 计算日期范围内的所有日期
            long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
            
            // 删除指定日期范围和科室的现有排班
            deleteSchedulesByDateRangeAndDepartment(startDate, endDate, department);
            
            // 根据模板和日期范围生成排班数据
            for (long i = 0; i < daysBetween; i++) {
                LocalDate currentDate = start.plusDays(i);
                String currentDateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                // 计算当前日期是星期几
                String dayOfWeek = getDayOfWeek(currentDate);
                String dayKey = dayOfWeek.toLowerCase();
                
                // 为每一天的每个班次创建排班记录
                for (Map<String, Object> shiftTemplate : template) {
                    String shift = (String) shiftTemplate.get("time");
                    String description = (String) shiftTemplate.get("description");
                    
                    // 根据星期几获取对应的医生ID
                    Object doctorId = shiftTemplate.get(dayKey);
                    if (doctorId != null && !doctorId.toString().isEmpty() && !doctorId.toString().equals("休息")) {
                        DoctorSchedule schedule = new DoctorSchedule();
                        schedule.setDoctorId(doctorId.toString());
                        schedule.setDepartment(department);
                        schedule.setScheduleDate(currentDateStr);
                        schedule.setShiftType(shift + (description != null ? " " + description : ""));
                        
                        // 根据班次类型设置开始和结束时间
                        if (shift.contains("白班")) {
                            schedule.setStartTime("08:00:00");
                            schedule.setEndTime("16:00:00");
                        } else if (shift.contains("中班")) {
                            schedule.setStartTime("16:00:00");
                            schedule.setEndTime("00:00:00");
                        } else if (shift.contains("夜班")) {
                            schedule.setStartTime("00:00:00");
                            schedule.setEndTime("08:00:00");
                        } else {
                            // 默认时间设置
                            schedule.setStartTime("08:00:00");
                            schedule.setEndTime("16:00:00");
                        }
                        
                        scheduleMapper.insert(schedule);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据日期范围和科室查询排班
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param department 科室
     * @return 排班列表
     */
    public List<DoctorSchedule> getSchedulesByDateRangeAndDepartment(String startDate, String endDate, String department) {
        if (department == null || department.isEmpty() || "全部科室".equals(department)) {
            return scheduleMapper.findByDateRangeAndType(startDate, endDate, "doctor");
        } else {
            return scheduleMapper.findByDateRangeAndTypeAndDepartment(startDate, endDate, "doctor", department);
        }
    }
    
    /**
     * 根据日期和科室查询排班
     * @param date 日期
     * @param department 科室
     * @return 排班列表
     */
    public List<DoctorSchedule> getSchedulesByDateAndDepartment(String date, String department) {
        if (department == null || department.isEmpty() || "全部科室".equals(department)) {
            return scheduleMapper.findByDate(date);
        } else {
            return scheduleMapper.findByDateAndDepartment(date, department);
        }
    }
    
    /**
     * 根据日期范围删除排班
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param department 科室
     * @return 删除的记录数
     */
    public int deleteSchedulesByDateRangeAndDepartment(String startDate, String endDate, String department) {
        // 这里需要一个自定义的删除方法，但因为MyBatis Plus没有直接支持范围删除
        // 我们先查询再删除，或者使用自定义SQL
        List<DoctorSchedule> schedules = getSchedulesByDateRangeAndDepartment(startDate, endDate, department);
        int count = 0;
        for (DoctorSchedule schedule : schedules) {
            scheduleMapper.deleteById(schedule.getId());
            count++;
        }
        return count;
    }
    
    /**
     * 获取星期几的中文名称
     * @param date 日期
     * @return 星期几
     */
    private String getDayOfWeek(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }
    
    /**
     * 获取一周的排班模板
     * @param department 科室
     * @return 排班模板
     */
    public List<Map<String, Object>> getWeeklyScheduleTemplate(String department) {
        // 查询最近的排班数据作为模板
        List<DoctorSchedule> recentSchedules = scheduleMapper.findByDateRangeAndTypeAndDepartment(
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            LocalDate.now().plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            "doctor", department
        );
        
        // 构建模板
        List<Map<String, Object>> template = new ArrayList<>();
        
        // 按班次分组
        Map<String, Map<String, Object>> shiftMap = new HashMap<>();
        for (DoctorSchedule schedule : recentSchedules) {
            String shift = schedule.getShiftType();
            if (!shiftMap.containsKey(shift)) {
                Map<String, Object> shiftTemplate = new HashMap<>();
                shiftTemplate.put("time", getShiftName(shift));
                shiftTemplate.put("description", getShiftDescription(shift));
                shiftTemplate.put("monday", "");
                shiftTemplate.put("tuesday", "");
                shiftTemplate.put("wednesday", "");
                shiftTemplate.put("thursday", "");
                shiftTemplate.put("friday", "");
                shiftTemplate.put("saturday", "");
                shiftTemplate.put("sunday", "");
                shiftMap.put(shift, shiftTemplate);
            }
            Map<String, Object> shiftTemplate = shiftMap.get(shift);
            shiftTemplate.put(getDayOfWeekFromScheduleDate(schedule.getScheduleDate()).toLowerCase(), 
                           schedule.getDoctorId());
        }
        
        template.addAll(shiftMap.values());
        
        // 如果没有数据，返回默认模板
        if (template.isEmpty()) {
            Map<String, Object> morningShift = new HashMap<>();
            morningShift.put("time", "白班");
            morningShift.put("description", "08:00-16:00");
            morningShift.put("monday", "");
            morningShift.put("tuesday", "");
            morningShift.put("wednesday", "");
            morningShift.put("thursday", "");
            morningShift.put("friday", "");
            morningShift.put("saturday", "");
            morningShift.put("sunday", "");
            
            Map<String, Object> afternoonShift = new HashMap<>();
            afternoonShift.put("time", "中班");
            afternoonShift.put("description", "16:00-00:00");
            afternoonShift.put("monday", "");
            afternoonShift.put("tuesday", "");
            afternoonShift.put("wednesday", "");
            afternoonShift.put("thursday", "");
            afternoonShift.put("friday", "");
            afternoonShift.put("saturday", "");
            afternoonShift.put("sunday", "");
            
            Map<String, Object> nightShift = new HashMap<>();
            nightShift.put("time", "夜班");
            nightShift.put("description", "00:00-08:00");
            nightShift.put("monday", "");
            nightShift.put("tuesday", "");
            nightShift.put("wednesday", "");
            nightShift.put("thursday", "");
            nightShift.put("friday", "");
            nightShift.put("saturday", "");
            nightShift.put("sunday", "");
            
            template.add(morningShift);
            template.add(afternoonShift);
            template.add(nightShift);
        }
        
        return template;
    }
    
    /**
     * 从班次类型中提取班次名称
     * @param shiftType 班次类型
     * @return 班次名称
     */
    private String getShiftName(String shiftType) {
        if (shiftType.contains("白班")) return "白班";
        if (shiftType.contains("中班")) return "中班";
        if (shiftType.contains("夜班")) return "夜班";
        return shiftType;
    }
    
    /**
     * 从班次类型中提取班次描述
     * @param shiftType 班次类型
     * @return 班次描述
     */
    private String getShiftDescription(String shiftType) {
        if (shiftType.contains("08:00-16:00")) return "08:00-16:00";
        if (shiftType.contains("16:00-00:00")) return "16:00-00:00";
        if (shiftType.contains("00:00-08:00")) return "00:00-08:00";
        return "";
    }
    
    /**
     * 从日期字符串获取星期几
     * @param dateStr 日期字符串
     * @return 星期几
     */
    private String getDayOfWeekFromScheduleDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return getDayOfWeek(date);
    }
}