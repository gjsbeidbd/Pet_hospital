package edu.mycc.xhd.pethospitalproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 宠物品种实体类
 */
@TableName("pet_breeds")
public class PetBreed {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 种类 ID
     */
    @TableField("species_id")
    private Long speciesId;
    
    /**
     * 品种名称
     */
    private String breedName;
    
    /**
     * 品种描述
     */
    private String description;
    
    /**
     * 品种图片路径
     */
    private String image;
    
    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSpeciesId() {
        return speciesId;
    }
    
    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }
    
    public String getBreedName() {
        return breedName;
    }
    
    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
