package edu.mycc.xhd.pethospitalproject.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.mycc.xhd.pethospitalproject.entity.FeeItem;
import edu.mycc.xhd.pethospitalproject.mapper.FeeItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 费用管理 Service 实现类
 */
@Service
public class FeeItemService extends ServiceImpl<FeeItemMapper, FeeItem> {
    
    /**
     * 获取所有启用的费用项目
     */
    public List<FeeItem> getActiveFeeItems() {
        return lambdaQuery()
                .eq(FeeItem::getIsActive, true)
                .orderByAsc(FeeItem::getCategory)
                .orderByAsc(FeeItem::getItemName)
                .list();
    }
    
    /**
     * 根据类别查询费用项目
     */
    public List<FeeItem> getFeeItemsByCategory(String category) {
        return lambdaQuery()
                .eq(FeeItem::getCategory, category)
                .eq(FeeItem::getIsActive, true)
                .orderByAsc(FeeItem::getItemName)
                .list();
    }
}
