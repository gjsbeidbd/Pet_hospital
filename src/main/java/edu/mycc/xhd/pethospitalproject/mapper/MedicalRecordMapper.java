package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.MedicalRecord;
import edu.mycc.xhd.pethospitalproject.entity.MedicalRecordDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {
    
    /**
     * 根据宠物ID获取病历列表
     * @param petId 宠物ID
     * @return 病历列表
     */
    @Select("SELECT * FROM medical_records WHERE pet_id = #{petId} ORDER BY visit_date DESC")
    List<MedicalRecord> selectByPetId(@Param("petId") Long petId);
    
    /**
     * 根据医生ID获取病历列表
     * @param doctorId 医生ID
     * @return 病历列表
     */
    @Select("SELECT * FROM medical_records WHERE doctor_id = #{doctorId} ORDER BY visit_date DESC")
    List<MedicalRecord> selectByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 根据ID和宠物ID获取病历信息
     * @param id 病历ID
     * @param petId 宠物ID
     * @return 病历信息
     */
    @Select("SELECT * FROM medical_records WHERE id = #{id} AND pet_id = #{petId}")
    MedicalRecord selectByIdAndPetId(@Param("id") Long id, @Param("petId") Long petId);
    
    /**
     * 根据 ID 和医生 ID 获取病历信息
     * @param id 病历 ID
     * @param doctorId 医生 ID
     * @return 病历信息
     */
    @Select("SELECT * FROM medical_records WHERE id = #{id} AND doctor_id = #{doctorId}")
    MedicalRecord selectByIdAndDoctorId(@Param("id") Long id, @Param("doctorId") Long doctorId);

    /**
     * 根据医生ID获取病历详情（联合查询，包含宠物、主人、医生信息）
     * @param doctorId 医生ID
     * @return 病历详情列表
     */
    @Select("SELECT " +
            "mr.id, mr.appointment_id AS appointmentId, mr.visit_date AS visitDate, mr.diagnosis, mr.treatment, mr.prescription, " +
            "mr.examination, mr.examination_result AS examinationResult, mr.surgery, mr.surgery_result AS surgeryResult, " +
            "mr.notes, mr.follow_up_required AS followUpRequired, mr.follow_up_date AS followUpDate, " +
            "p.id AS petId, p.name AS petName, p.breed AS petBreed, p.age AS petAge, p.gender AS petGender, " +
            "u.id AS ownerId, u.name AS ownerName, u.phone AS ownerPhone, " +
            "d.id AS doctorId, d.name AS doctorName, d.department " +
            "FROM medical_records mr " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN doctors d ON mr.doctor_id = d.id " +
            "WHERE mr.doctor_id = #{doctorId} " +
            "ORDER BY mr.visit_date DESC")
    List<MedicalRecordDetail> selectDetailByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 根据宠物 ID 获取病历详情（联合查询，包含宠物、主人、医生信息）
     * @param petId 宠物 ID
     * @return 病历详情列表
     */
    @Select("SELECT " +
            "mr.id, mr.visit_date AS visitDate, mr.diagnosis, mr.treatment, mr.prescription, " +
            "mr.examination, mr.examination_result AS examinationResult, mr.surgery, mr.surgery_result AS surgeryResult, " +
            "mr.notes, mr.follow_up_required AS followUpRequired, mr.follow_up_date AS followUpDate, " +
            "p.id AS petId, p.name AS petName, p.breed AS petBreed, p.age AS petAge, p.gender AS petGender, " +
            "u.id AS ownerId, u.name AS ownerName, u.phone AS ownerPhone, " +
            "d.id AS doctorId, d.name AS doctorName, d.department " +
            "FROM medical_records mr " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN doctors d ON mr.doctor_id = d.id " +
            "WHERE mr.pet_id = #{petId} " +
            "ORDER BY mr.visit_date DESC")
    List<MedicalRecordDetail> selectDetailByPetId(@Param("petId") Long petId);
    
    /**
     * 获取所有病历详情（联合查询，包含宠物、主人、医生信息）
     * @return 病历详情列表
     */
    @Select("SELECT " +
            "mr.id, mr.visit_date AS visitDate, mr.diagnosis, mr.treatment, mr.prescription, " +
            "mr.examination, mr.examination_result AS examinationResult, mr.surgery, mr.surgery_result AS surgeryResult, " +
            "mr.notes, mr.follow_up_required AS followUpRequired, mr.follow_up_date AS followUpDate, " +
            "p.id AS petId, p.name AS petName, p.breed AS petBreed, p.age AS petAge, p.gender AS petGender, " +
            "u.id AS ownerId, u.name AS ownerName, u.phone AS ownerPhone, " +
            "d.id AS doctorId, d.name AS doctorName, d.department " +
            "FROM medical_records mr " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN doctors d ON mr.doctor_id = d.id " +
            "ORDER BY mr.visit_date DESC")
    List<MedicalRecordDetail> selectAllDetail();
    
    /**
     * 根据科室获取病历详情
     * @param department 科室
     * @return 病历详情列表
     */
    @Select("SELECT " +
            "mr.id, mr.visit_date AS visitDate, mr.diagnosis, mr.treatment, mr.prescription, " +
            "mr.examination, mr.examination_result AS examinationResult, mr.surgery, mr.surgery_result AS surgeryResult, " +
            "mr.notes, mr.follow_up_required AS followUpRequired, mr.follow_up_date AS followUpDate, " +
            "p.id AS petId, p.name AS petName, p.breed AS petBreed, p.age AS petAge, p.gender AS petGender, " +
            "u.id AS ownerId, u.name AS ownerName, u.phone AS ownerPhone, " +
            "d.id AS doctorId, d.name AS doctorName, d.department " +
            "FROM medical_records mr " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN doctors d ON mr.doctor_id = d.id " +
            "WHERE d.department = #{department} " +
            "ORDER BY mr.visit_date DESC")
    List<MedicalRecordDetail> selectDetailByDepartment(@Param("department") String department);
}