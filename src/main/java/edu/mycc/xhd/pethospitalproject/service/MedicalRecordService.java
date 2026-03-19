package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.MedicalRecord;
import edu.mycc.xhd.pethospitalproject.entity.MedicalRecordDetail;
import edu.mycc.xhd.pethospitalproject.mapper.MedicalRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    /**
     * 根据宠物ID获取病历列表
     * @param petId 宠物ID
     * @return 病历列表
     */
    public List<MedicalRecord> getMedicalRecordsByPetId(Long petId) {
        return medicalRecordMapper.selectByPetId(petId);
    }

    /**
     * 根据医生ID获取病历列表
     * @param doctorId 医生ID
     * @return 病历列表
     */
    public List<MedicalRecord> getMedicalRecordsByDoctorId(Long doctorId) {
        return medicalRecordMapper.selectByDoctorId(doctorId);
    }

    /**
     * 添加病历
     * @param medicalRecord 病历信息
     * @return 是否添加成功
     */
    public boolean addMedicalRecord(MedicalRecord medicalRecord) {
        int result = medicalRecordMapper.insert(medicalRecord);
        return result > 0;
    }

    /**
     * 更新病历信息
     * @param medicalRecord 病历信息
     * @return 是否更新成功
     */
    public boolean updateMedicalRecord(MedicalRecord medicalRecord) {
        int result = medicalRecordMapper.updateById(medicalRecord);
        return result > 0;
    }

    /**
     * 删除病历
     * @param id 病历ID
     * @return 是否删除成功
     */
    public boolean deleteMedicalRecord(Long id) {
        int result = medicalRecordMapper.deleteById(id);
        return result > 0;
    }

    /**
     * 根据ID获取病历信息
     * @param id 病历ID
     * @return 病历信息
     */
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordMapper.selectById(id);
    }

    /**
     * 根据ID和宠物ID获取病历信息
     * @param id 病历ID
     * @param petId 宠物ID
     * @return 病历信息
     */
    public MedicalRecord getMedicalRecordByIdAndPetId(Long id, Long petId) {
        return medicalRecordMapper.selectByIdAndPetId(id, petId);
    }

    /**
     * 根据 ID 和医生 ID 获取病历信息
     * @param id 病历 ID
     * @param doctorId 医生 ID
     * @return 病历信息
     */
    public MedicalRecord getMedicalRecordByIdAndDoctorId(Long id, Long doctorId) {
        return medicalRecordMapper.selectByIdAndDoctorId(id, doctorId);
    }
    
    /**
     * 根据宠物 ID 获取病历详情（包含宠物、主人、医生信息）
     * @param petId 宠物 ID
     * @return 病历详情列表
     */
    public List<MedicalRecordDetail> getMedicalRecordDetailByPetId(Long petId) {
        return medicalRecordMapper.selectDetailByPetId(petId);
    }
    
    /**
     * 获取所有病历详情
     * @return 病历详情列表
     */
    public List<MedicalRecordDetail> getAllMedicalRecordDetail() {
        return medicalRecordMapper.selectAllDetail();
    }
    
    /**
     * 根据科室获取病历详情
     * @param department 科室
     * @return 病历详情列表
     */
    public List<MedicalRecordDetail> getMedicalRecordDetailByDepartment(String department) {
        return medicalRecordMapper.selectDetailByDepartment(department);
    }
}