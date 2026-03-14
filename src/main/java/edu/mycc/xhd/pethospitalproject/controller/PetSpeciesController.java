package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.PetSpecies;
import edu.mycc.xhd.pethospitalproject.service.PetSpeciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物种类管理 Controller
 */
@RestController
@RequestMapping("/api/pet-species")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
public class PetSpeciesController {
    
    @Autowired
    private PetSpeciesService petSpeciesService;
    
    /**
     * 获取所有宠物种类
     */
    @GetMapping
    public List<PetSpecies> getPetSpecies() {
        return petSpeciesService.getAllPetSpecies();
    }
    
    /**
     * 根据 ID 获取宠物种类
     */
    @GetMapping("/{id}")
    public PetSpecies getPetSpeciesById(@PathVariable Long id) {
        return petSpeciesService.getById(id);
    }
    
    /**
     * 新增宠物种类
     */
    @PostMapping
    public PetSpecies addPetSpecies(@RequestBody PetSpecies petSpecies) {
        petSpecies.setIsActive(true);
        petSpeciesService.save(petSpecies);
        return petSpeciesService.getById(petSpecies.getId());
    }
    
    /**
     * 更新宠物种类
     */
    @PutMapping("/{id}")
    public PetSpecies updatePetSpecies(@PathVariable Long id, @RequestBody PetSpecies petSpecies) {
        petSpecies.setId(id);
        petSpeciesService.updateById(petSpecies);
        return petSpeciesService.getById(id);
    }
    
    /**
     * 删除宠物种类
     */
    @DeleteMapping("/{id}")
    public void deletePetSpecies(@PathVariable Long id) {
        petSpeciesService.removeById(id);
    }
}
