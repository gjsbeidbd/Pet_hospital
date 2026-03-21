package edu.mycc.xhd.pethospitalproject.entity;

import java.time.LocalDate;

/**
 * 病历详情 DTO（包含宠物主人、医生信息）
 */
public class MedicalRecordDetail {
    // 病历信息
    private Long id;
    private Long appointmentId;
    private LocalDate visitDate;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private String examination;
    private String examinationResult;
    private String surgery;
    private String surgeryResult;
    private String notes;
    private Boolean followUpRequired;
    private LocalDate followUpDate;
    
    // 宠物信息
    private Long petId;
    private String petName;
    private String petBreed;
    private Integer petAge;
    private String petGender;
    
    // 宠物主人信息
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    
    // 医生信息
    private Long doctorId;
    private String doctorName;
    private String department;
    
    // Constructors
    public MedicalRecordDetail() {}
    
    public MedicalRecordDetail(Long id, Long appointmentId, LocalDate visitDate, String diagnosis, String treatment,
                               String prescription, String examination, String examinationResult,
                               String surgery, String surgeryResult, String notes,
                               Boolean followUpRequired, LocalDate followUpDate,
                               Long petId, String petName, String petBreed, Integer petAge, String petGender,
                               Long ownerId, String ownerName, String ownerPhone,
                               Long doctorId, String doctorName, String department) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.visitDate = visitDate;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.examination = examination;
        this.examinationResult = examinationResult;
        this.surgery = surgery;
        this.surgeryResult = surgeryResult;
        this.notes = notes;
        this.followUpRequired = followUpRequired;
        this.followUpDate = followUpDate;
        this.petId = petId;
        this.petName = petName;
        this.petBreed = petBreed;
        this.petAge = petAge;
        this.petGender = petGender;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.department = department;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }
    
    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getTreatment() {
        return treatment;
    }
    
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    
    public String getPrescription() {
        return prescription;
    }
    
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
    
    public String getExamination() {
        return examination;
    }
    
    public void setExamination(String examination) {
        this.examination = examination;
    }
    
    public String getExaminationResult() {
        return examinationResult;
    }
    
    public void setExaminationResult(String examinationResult) {
        this.examinationResult = examinationResult;
    }
    
    public String getSurgery() {
        return surgery;
    }
    
    public void setSurgery(String surgery) {
        this.surgery = surgery;
    }
    
    public String getSurgeryResult() {
        return surgeryResult;
    }
    
    public void setSurgeryResult(String surgeryResult) {
        this.surgeryResult = surgeryResult;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }
    
    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }
    
    public LocalDate getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public Long getPetId() {
        return petId;
    }
    
    public void setPetId(Long petId) {
        this.petId = petId;
    }
    
    public String getPetName() {
        return petName;
    }
    
    public void setPetName(String petName) {
        this.petName = petName;
    }
    
    public String getPetBreed() {
        return petBreed;
    }
    
    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }
    
    public Integer getPetAge() {
        return petAge;
    }
    
    public void setPetAge(Integer petAge) {
        this.petAge = petAge;
    }
    
    public String getPetGender() {
        return petGender;
    }
    
    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getOwnerPhone() {
        return ownerPhone;
    }
    
    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}
