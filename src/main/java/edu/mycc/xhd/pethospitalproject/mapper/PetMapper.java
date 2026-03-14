package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
    
    /**
     * 根据用户ID获取宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    @Select("SELECT * FROM pets WHERE user_id = #{userId}")
    List<Pet> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID和用户ID获取宠物信息
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 宠物信息
     */
    @Select("SELECT * FROM pets WHERE id = #{id} AND user_id = #{userId}")
    Pet selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}