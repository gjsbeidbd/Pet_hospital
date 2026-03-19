package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.DoctorSchedule;
import edu.mycc.xhd.pethospitalproject.mapper.DoctorScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorScheduleService extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> {
    
    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;
    
    public List<DoctorSchedule> getSchedulesByDateRangeAndDepartment(String startDate, String endDate, String department) {
        return doctorScheduleMapper.findByDateRangeAndDepartment(startDate, endDate, department);
    }
    
    public void deleteByDateRange(String startDate, String endDate) {
        doctorScheduleMapper.deleteByDateRange(startDate, endDate);
    }
    
    public List<DoctorSchedule> getSchedulesByDoctorIdAndDateRange(Long doctorId, String startDate, String endDate) {
        return doctorScheduleMapper.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
    }

    public DoctorSchedule findByDepartmentAndDateAndShift(String department, String scheduleDate, String shiftType) {
        return doctorScheduleMapper.findByDepartmentAndDateAndShift(department, scheduleDate, shiftType);
    }
}
