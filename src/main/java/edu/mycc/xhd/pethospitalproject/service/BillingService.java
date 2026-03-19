package edu.mycc.xhd.pethospitalproject.service;

import edu.mycc.xhd.pethospitalproject.entity.Billing;
import edu.mycc.xhd.pethospitalproject.entity.BillingItem;
import edu.mycc.xhd.pethospitalproject.entity.DrugInventory;
import edu.mycc.xhd.pethospitalproject.mapper.BillingItemMapper;
import edu.mycc.xhd.pethospitalproject.mapper.BillingMapper;
import edu.mycc.xhd.pethospitalproject.mapper.DrugInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    @Autowired
    private BillingMapper billingMapper;

    @Autowired
    private BillingItemMapper billingItemMapper;

    @Autowired
    private DrugInventoryMapper drugInventoryMapper;

    public List<Billing> getPendingBillings() {
        return billingMapper.selectPendingBillings();
    }

    public int getPendingBillingCount() {
        return billingMapper.selectPendingBillingCount();
    }

    public BigDecimal getTodayRevenue() {
        return billingMapper.selectTodayRevenue();
    }

    public List<Map<String, Object>> getLastSevenDaysRevenue() {
        List<Map<String, Object>> rawData = billingMapper.selectLastSevenDaysRevenue();
        Map<String, BigDecimal> revenueMap = new java.util.LinkedHashMap<>();

        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            revenueMap.put(date, BigDecimal.ZERO);
        }

        for (Map<String, Object> row : rawData) {
            String date = row.get("date").toString();
            BigDecimal amount = (BigDecimal) row.get("total");
            revenueMap.put(date, amount);
        }

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : revenueMap.entrySet()) {
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("date", entry.getKey());
            item.put("total", entry.getValue());
            result.add(item);
        }
        return result;
    }

    public List<Billing> getPendingByUserId(Long userId) {
        return billingMapper.selectPendingByUserId(userId);
    }

    public boolean processPayment(Long id, String paymentMethod) {
        return billingMapper.updatePayment(id, paymentMethod) > 0;
    }

    @Transactional
    public boolean addBilling(Billing billing, List<BillingItem> items) {
        int result = billingMapper.insert(billing);
        if (result > 0 && items != null && !items.isEmpty()) {
            for (BillingItem item : items) {
                item.setBillingId(billing.getId());
                billingItemMapper.insert(item);
            }
        }
        return result > 0;
    }

    public List<BillingItem> getBillingItems(Long billingId) {
        return billingItemMapper.selectByBillingId(billingId);
    }

    public Billing getBillingById(Long id) {
        return billingMapper.selectById(id);
    }

    @Transactional
    public boolean deductDrugInventory(Long billingId) {
        List<BillingItem> items = billingItemMapper.selectByBillingId(billingId);
        for (BillingItem item : items) {
            if ("药品费".equals(item.getCategory()) && item.getItemName() != null) {
                List<DrugInventory> drugs = drugInventoryMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DrugInventory>()
                        .eq("name", item.getItemName())
                );
                if (!drugs.isEmpty()) {
                    DrugInventory drug = drugs.get(0);
                    int newStock = drug.getStock() - item.getQuantity();
                    drug.setStock(Math.max(newStock, 0));
                    drugInventoryMapper.updateById(drug);
                }
            }
        }
        return true;
    }
}
