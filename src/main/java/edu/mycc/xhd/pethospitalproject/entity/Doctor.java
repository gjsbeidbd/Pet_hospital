package edu.mycc.xhd.pethospitalproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("doctors")
public class Doctor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String employeeId;
    private String name;
    private String birthday;
    private String idCard;
    private String address;
    private String department;
    private String position; // 职称（如：住院医师、主治医师等）
    private String phone;
    private String email;
    private String avatar;
    private String hireDate;
    private String password;
    private String status;
    
    // Constructors
    public Doctor() {}
    
    public Doctor(Long id, String employeeId, String name, String birthday, String idCard, String address,
                 String department, String position, String phone, String email, String avatar, String hireDate,
                 String password, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.birthday = birthday;
        this.idCard = idCard;
        this.address = address;
        this.department = department;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.hireDate = hireDate;
        this.password = password;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", idCard='" + idCard + '\'' +
                ", address='" + address + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}