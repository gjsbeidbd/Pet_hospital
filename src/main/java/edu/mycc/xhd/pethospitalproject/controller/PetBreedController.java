package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.PetBreed;
import edu.mycc.xhd.pethospitalproject.service.PetBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物品种管理 Controller
 */
@RestController
@RequestMapping("/api/pet-breeds")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
public class PetBreedController {
    
    @Autowired
    private PetBreedService petBreedService;
    
    /**
     * 根据种类 ID 获取品种列表
     */
    @GetMapping
    public List<PetBreed> getBreedsBySpeciesId(@RequestParam Long speciesId) {
        return petBreedService.getBreedsBySpeciesId(speciesId);
    }
    
    /**
     * 根据 ID 获取宠物品种
     */
    @GetMapping("/{id}")
    public PetBreed getPetBreedById(@PathVariable Long id) {
        return petBreedService.getById(id);
    }
    
    /**
     * 新增宠物品种
     */
    @PostMapping
    public PetBreed addPetBreed(@RequestBody PetBreed petBreed) {
        petBreed.setIsActive(true);
        petBreedService.save(petBreed);
        return petBreedService.getById(petBreed.getId());
    }
    
    /**
     * 更新宠物品种
     */
    @PutMapping("/{id}")
    public PetBreed updatePetBreed(@PathVariable Long id, @RequestBody PetBreed petBreed) {
        petBreed.setId(id);
        petBreedService.updateById(petBreed);
        return petBreedService.getById(id);
    }
    
    /**
     * 删除宠物品种
     */
    @DeleteMapping("/{id}")
    public void deletePetBreed(@PathVariable Long id) {
        petBreedService.removeById(id);
    }
}
