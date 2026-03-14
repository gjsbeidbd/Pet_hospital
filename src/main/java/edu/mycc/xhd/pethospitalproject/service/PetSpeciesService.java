package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.PetSpecies;
import edu.mycc.xhd.pethospitalproject.mapper.PetSpeciesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物种类 Service 实现类
 */
@Service
public class PetSpeciesService extends ServiceImpl<PetSpeciesMapper, PetSpecies> {
    
    /**
     * 获取所有宠物种类
     */
    public List<PetSpecies> getAllPetSpecies() {
        return lambdaQuery()
                .orderByAsc(PetSpecies::getSpeciesName)
                .list();
    }
}
