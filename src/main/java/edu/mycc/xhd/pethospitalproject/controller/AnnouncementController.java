package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Announcement;
import edu.mycc.xhd.pethospitalproject.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:5180")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<List<Announcement>> getActiveAnnouncements(
            @RequestParam(required = false) String category) {
        List<Announcement> announcements;
        if (category == null || category.isEmpty()) {
            announcements = announcementService.lambdaQuery()
                    .eq(Announcement::getIsActive, true)
                    .orderByDesc(Announcement::getPublishDate)
                    .list();
        } else {
            announcements = announcementService.lambdaQuery()
                    .eq(Announcement::getIsActive, true)
                    .and(w -> w.eq(Announcement::getCategory, category).or().eq(Announcement::getCategory, "ALL"))
                    .orderByDesc(Announcement::getPublishDate)
                    .list();
        }
        return ResponseEntity.ok(announcements);
    }

    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        announcement.setIsActive(true);
        if (announcement.getPublishDate() == null) {
            announcement.setPublishDate(LocalDateTime.now());
        }
        announcementService.save(announcement);
        return ResponseEntity.ok(announcement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        return ResponseEntity.ok().build();
    }
}