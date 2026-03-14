package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.FeeItem;
import edu.mycc.xhd.pethospitalproject.service.FeeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用管理 Controller
 */
@RestController
@RequestMapping("/api/fee-items")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
public class FeeItemController {
    
    @Autowired
    private FeeItemService feeItemService;
    
    /**
     * 获取所有费用项目列表
     */
    @GetMapping
    public List<FeeItem> getFeeItems() {
        return feeItemService.getActiveFeeItems();
    }
    
    /**
     * 根据 ID 获取费用项目
     */
    @GetMapping("/{id}")
    public FeeItem getFeeItemById(@PathVariable Long id) {
        return feeItemService.getById(id);
    }
    
    /**
     * 新增费用项目
     */
    @PostMapping
    public FeeItem addFeeItem(@RequestBody FeeItem feeItem) {
        feeItem.setIsActive(true);
        feeItemService.save(feeItem);
        return feeItem;
    }
    
    /**
     * 更新费用项目
     */
    @PutMapping("/{id}")
    public FeeItem updateFeeItem(@PathVariable Long id, @RequestBody FeeItem feeItem) {
        feeItem.setId(id);
        feeItemService.updateById(feeItem);
        return feeItemService.getById(id);
    }
    
    /**
     * 删除费用项目
     */
    @DeleteMapping("/{id}")
    public void deleteFeeItem(@PathVariable Long id) {
        feeItemService.removeById(id);
    }
}
