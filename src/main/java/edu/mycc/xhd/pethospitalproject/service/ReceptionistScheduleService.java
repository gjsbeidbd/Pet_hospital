package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.ReceptionistSchedule;
import edu.mycc.xhd.pethospitalproject.mapper.ReceptionistScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceptionistScheduleService extends ServiceImpl<ReceptionistScheduleMapper, ReceptionistSchedule> {
    
    @Autowired
    private ReceptionistScheduleMapper receptionistScheduleMapper;
    
    public List<ReceptionistSchedule> getSchedulesByDateRangeAndDepartment(String startDate, String endDate, String department) {
        return receptionistScheduleMapper.findByDateRangeAndDepartment(startDate, endDate, department);
    }
    
    public void deleteByDateRange(String startDate, String endDate) {
        receptionistScheduleMapper.deleteByDateRange(startDate, endDate);
    }
    
    public List<ReceptionistSchedule> getSchedulesByReceptionistIdAndDateRange(Long receptionistId, String startDate, String endDate) {
        return receptionistScheduleMapper.findByReceptionistIdAndDateRange(receptionistId, startDate, endDate);
    }
}
