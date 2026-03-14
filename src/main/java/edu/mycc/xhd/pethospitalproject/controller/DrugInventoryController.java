package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.DrugInventory;
import edu.mycc.xhd.pethospitalproject.service.DrugInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品库存管理 Controller
 */
@RestController
@RequestMapping("/api/drug-inventory")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
public class DrugInventoryController {
    
    @Autowired
    private DrugInventoryService drugInventoryService;
    
    /**
     * 获取所有药品
     */
    @GetMapping
    public List<DrugInventory> getDrugs() {
        return drugInventoryService.getAllDrugs();
    }
    
    /**
     * 新增药品
     */
    @PostMapping
    public DrugInventory addDrug(@RequestBody DrugInventory drug) {
        // 自动生成编号
        List<DrugInventory> allDrugs = drugInventoryService.list();
        int maxCode = allDrugs.stream()
                .mapToInt(d -> {
                    try {
                        return Integer.parseInt(d.getCode().replace("D", ""));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        
        String newCode = "D" + String.format("%03d", maxCode + 1);
        drug.setCode(newCode);
        drug.setIsActive(true);
        drugInventoryService.save(drug);
        return drug;
    }
    
    /**
     * 更新药品
     */
    @PutMapping("/{id}")
    public DrugInventory updateDrug(@PathVariable Long id, @RequestBody DrugInventory drug) {
        drug.setId(id);
        drugInventoryService.updateById(drug);
        return drug;
    }
    
    /**
     * 删除药品
     */
    @DeleteMapping("/{id}")
    public void deleteDrug(@PathVariable Long id) {
        drugInventoryService.removeById(id);
    }
}
