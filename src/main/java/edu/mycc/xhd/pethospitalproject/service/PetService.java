package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.Pet;
import edu.mycc.xhd.pethospitalproject.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetMapper petMapper;

    /**
     * 根据用户ID获取宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    public List<Pet> getPetsByUserId(Long userId) {
        return petMapper.selectByUserId(userId);
    }

    public List<Pet> getAllPets() {
        return petMapper.selectList(null);
    }

    /**
     * 添加宠物
     * @param pet 宠物信息
     * @return 是否添加成功
     */
    public boolean addPet(Pet pet) {
        int result = petMapper.insert(pet);
        return result > 0;
    }

    /**
     * 更新宠物信息
     * @param pet 宠物信息
     * @return 是否更新成功
     */
    public boolean updatePet(Pet pet) {
        int result = petMapper.updateById(pet);
        return result > 0;
    }

    /**
     * 删除宠物
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deletePet(Long id, Long userId) {
        Pet pet = petMapper.selectByIdAndUserId(id, userId);
        if (pet != null) {
            int result = petMapper.deleteById(id);
            return result > 0;
        }
        return false;
    }

    /**
     * 根据ID和用户ID获取宠物信息
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 宠物信息
     */
    public Pet getPetByIdAndUserId(Long id, Long userId) {
        return petMapper.selectByIdAndUserId(id, userId);
    }
}