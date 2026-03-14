package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.MedicalRecord;
import edu.mycc.xhd.pethospitalproject.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * 获取宠物病历列表
     * @param petId 宠物ID
     * @return 病历列表
     */
    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByPetId(@RequestParam Long petId) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByPetId(petId);
        return ResponseEntity.ok(medicalRecords);
    }

    /**
     * 获取医生病历列表
     * @param doctorId 医生ID
     * @return 病历列表
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByDoctorId(@RequestParam Long doctorId) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByDoctorId(doctorId);
        return ResponseEntity.ok(medicalRecords);
    }

    /**
     * 添加病历
     * @param medicalRecord 病历信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<?> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        boolean success = medicalRecordService.addMedicalRecord(medicalRecord);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "病历添加成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "病历添加失败"));
        }
    }

    /**
     * 更新病历信息
     * @param id 病历ID
     * @param medicalRecord 病历信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord medicalRecord) {
        medicalRecord.setId(id);
        boolean success = medicalRecordService.updateMedicalRecord(medicalRecord);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "病历信息更新成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "病历信息更新失败"));
        }
    }

    /**
     * 删除病历
     * @param id 病历ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long id) {
        boolean success = medicalRecordService.deleteMedicalRecord(id);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "病历删除成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "病历删除失败"));
        }
    }

    /**
     * 获取病历信息
     * @param id 病历ID
     * @return 病历信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);
        if (medicalRecord != null) {
            return ResponseEntity.ok(medicalRecord);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取病历信息（根据ID和宠物ID）
     * @param id 病历ID
     * @param petId 宠物ID
     * @return 病历信息
     */
    @GetMapping("/{id}/pet")
    public ResponseEntity<?> getMedicalRecordByIdAndPetId(@PathVariable Long id, @RequestParam Long petId) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByIdAndPetId(id, petId);
        if (medicalRecord != null) {
            return ResponseEntity.ok(medicalRecord);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取病历信息（根据ID和医生ID）
     * @param id 病历ID
     * @param doctorId 医生ID
     * @return 病历信息
     */
    @GetMapping("/{id}/doctor")
    public ResponseEntity<?> getMedicalRecordByIdAndDoctorId(@PathVariable Long id, @RequestParam Long doctorId) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByIdAndDoctorId(id, doctorId);
        if (medicalRecord != null) {
            return ResponseEntity.ok(medicalRecord);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}