package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.MedicalRecord;
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
     * 根据ID和医生ID获取病历信息
     * @param id 病历ID
     * @param doctorId 医生ID
     * @return 病历信息
     */
    @Select("SELECT * FROM medical_records WHERE id = #{id} AND doctor_id = #{doctorId}")
    MedicalRecord selectByIdAndDoctorId(@Param("id") Long id, @Param("doctorId") Long doctorId);
}