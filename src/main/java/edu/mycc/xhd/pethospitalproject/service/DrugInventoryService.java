package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.DrugInventory;
import edu.mycc.xhd.pethospitalproject.mapper.DrugInventoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品库存 Service
 */
@Service
public class DrugInventoryService extends ServiceImpl<DrugInventoryMapper, DrugInventory> {
    
    /**
     * 获取所有启用的药品
     */
    public List<DrugInventory> getActiveDrugs() {
        return lambdaQuery()
                .eq(DrugInventory::getIsActive, true)
                .orderByDesc(DrugInventory::getCreatedAt)
                .list();
    }
    
    /**
     * 获取所有药品（包括禁用）
     */
    public List<DrugInventory> getAllDrugs() {
        return lambdaQuery()
                .orderByDesc(DrugInventory::getCreatedAt)
                .list();
    }
}
