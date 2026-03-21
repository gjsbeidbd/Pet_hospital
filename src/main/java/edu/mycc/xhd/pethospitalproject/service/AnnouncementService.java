package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.Announcement;
import edu.mycc.xhd.pethospitalproject.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {
}