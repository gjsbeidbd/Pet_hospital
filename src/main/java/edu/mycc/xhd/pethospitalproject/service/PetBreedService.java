package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.PetBreed;
import edu.mycc.xhd.pethospitalproject.mapper.PetBreedMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物品种 Service 实现类
 */
@Service
public class PetBreedService extends ServiceImpl<PetBreedMapper, PetBreed> {
    
    /**
     * 根据种类 ID 查询品种列表
     */
    public List<PetBreed> getBreedsBySpeciesId(Long speciesId) {
        return lambdaQuery()
                .eq(PetBreed::getSpeciesId, speciesId)
                .eq(PetBreed::getIsActive, true)
                .orderByAsc(PetBreed::getBreedName)
                .list();
    }
}
