package edu.mycc.xhd.pethospitalproject.controller;

import edu.mycc.xhd.pethospitalproject.entity.Billing;
import edu.mycc.xhd.pethospitalproject.entity.BillingItem;
import edu.mycc.xhd.pethospitalproject.service.BillingService;
import edu.mycc.xhd.pethospitalproject.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "http://localhost:5180")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/pending")
    public ResponseEntity<List<Billing>> getPendingBillings() {
        List<Billing> billings = billingService.getPendingBillings();
        return ResponseEntity.ok(billings);
    }

    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Object>> getPendingBillingCount() {
        int count = billingService.getPendingBillingCount();
        return ResponseEntity.ok(Map.of("data", count));
    }

    @GetMapping("/pending/user/{userId}")
    public ResponseEntity<List<Billing>> getPendingByUserId(@PathVariable Long userId) {
        List<Billing> billings = billingService.getPendingByUserId(userId);
        return ResponseEntity.ok(billings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billing> getBillingById(@PathVariable Long id) {
        Billing billing = billingService.getBillingById(id);
        if (billing != null) {
            return ResponseEntity.ok(billing);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<BillingItem>> getBillingItems(@PathVariable Long id) {
        List<BillingItem> items = billingService.getBillingItems(id);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> processPayment(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String paymentMethod = request.get("paymentMethod");
        boolean success = billingService.processPayment(id, paymentMethod);
        if (success) {
            Billing billing = billingService.getBillingById(id);
            if (billing != null && billing.getAppointmentId() != null) {
                appointmentService.updateAppointmentStatus(billing.getAppointmentId(), "completed");
            }
            billingService.deductDrugInventory(id);
            return ResponseEntity.ok(Map.of("message", "支付成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "支付失败"));
        }
    }

    @PostMapping
    public ResponseEntity<?> addBilling(@RequestBody Map<String, Object> request) {
        try {
            Billing billing = new Billing();
            billing.setUserId(Long.parseLong(request.get("userId").toString()));
            if (request.get("appointmentId") != null) {
                billing.setAppointmentId(Long.parseLong(request.get("appointmentId").toString()));
            }
            billing.setDescription((String) request.get("description"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            
            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            java.util.List<BillingItem> billingItems = new java.util.ArrayList<>();
            
            if (itemsData != null) {
                for (Map<String, Object> itemData : itemsData) {
                    BillingItem item = new BillingItem();
                    item.setItemName((String) itemData.get("itemName"));
                    item.setCategory((String) itemData.get("category"));
                    item.setQuantity(Integer.parseInt(itemData.get("quantity").toString()));
                    item.setUnitPrice(new java.math.BigDecimal(itemData.get("unitPrice").toString()));
                    item.setTotalPrice(new java.math.BigDecimal(itemData.get("totalPrice").toString()));
                    item.setUnit((String) itemData.get("unit"));
                    item.setDescription((String) itemData.get("description"));
                    billingItems.add(item);
                    totalAmount = totalAmount.add(item.getTotalPrice());
                }
            }
            
            billing.setAmount(totalAmount);

            boolean success = billingService.addBilling(billing, billingItems);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "账单创建成功", "billingId", billing.getId()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "账单创建失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "账单创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/today-revenue")
    public ResponseEntity<BigDecimal> getTodayRevenue() {
        BigDecimal revenue = billingService.getTodayRevenue();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/last-seven-days")
    public ResponseEntity<List<Map<String, Object>>> getLastSevenDaysRevenue() {
        List<Map<String, Object>> revenue = billingService.getLastSevenDaysRevenue();
        return ResponseEntity.ok(revenue);
    }
}
