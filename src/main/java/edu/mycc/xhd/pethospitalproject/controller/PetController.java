package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Pet;
import edu.mycc.xhd.pethospitalproject.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:5180") // 允许前端跨域访问
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取用户宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    @GetMapping
    public ResponseEntity<List<Pet>> getPetsByUserId(@RequestParam Long userId) {
        List<Pet> pets = petService.getPetsByUserId(userId);
        return ResponseEntity.ok(pets);
    }

    /**
     * 添加宠物
     * @param pet 宠物信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody Pet pet) {
        boolean success = petService.addPet(pet);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "宠物添加成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "宠物添加失败"));
        }
    }

    /**
     * 更新宠物信息
     * @param id 宠物ID
     * @param pet 宠物信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        boolean success = petService.updatePet(pet);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "宠物信息更新成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "宠物信息更新失败"));
        }
    }

    /**
     * 删除宠物
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = petService.deletePet(id, userId);
        if (success) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "宠物删除成功"));
        } else {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "宠物删除失败"));
        }
    }

    /**
     * 获取宠物信息
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 宠物信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Long id, @RequestParam Long userId) {
        Pet pet = petService.getPetByIdAndUserId(id, userId);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}