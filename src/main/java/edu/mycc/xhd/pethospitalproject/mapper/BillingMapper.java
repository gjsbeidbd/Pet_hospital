package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.Billing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface BillingMapper extends BaseMapper<Billing> {
    
    @Select("SELECT b.*, u.name AS userName, p.name AS petName, p.id AS petId " +
            "FROM billing b " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "LEFT JOIN appointments a ON b.appointment_id = a.id " +
            "LEFT JOIN pets p ON a.pet_id = p.id " +
            "WHERE b.status = 'pending' " +
            "ORDER BY b.created_at DESC")
    List<Billing> selectPendingBillings();
    
    @Select("SELECT b.*, u.name AS userName, p.name AS petName, p.id AS petId " +
            "FROM billing b " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "LEFT JOIN appointments a ON b.appointment_id = a.id " +
            "LEFT JOIN pets p ON a.pet_id = p.id " +
            "WHERE b.user_id = #{userId} AND b.status = 'pending' " +
            "ORDER BY b.created_at DESC")
    List<Billing> selectPendingByUserId(@Param("userId") Long userId);
    
    @Update("UPDATE billing SET status = 'paid', payment_method = #{paymentMethod}, " +
            "payment_date = NOW() WHERE id = #{id}")
    int updatePayment(@Param("id") Long id, @Param("paymentMethod") String paymentMethod);

    @Select("SELECT COUNT(*) FROM billing WHERE status = 'pending'")
    int selectPendingBillingCount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM billing WHERE status = 'paid' AND DATE(COALESCE(payment_date, created_at)) = CURRENT_DATE")
    BigDecimal selectTodayRevenue();

    @Select("SELECT DATE(COALESCE(payment_date, created_at)) as date, COALESCE(SUM(amount), 0) as total " +
            "FROM billing WHERE status = 'paid' AND COALESCE(payment_date, created_at) >= DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) " +
            "GROUP BY DATE(COALESCE(payment_date, created_at)) ORDER BY date")
    List<Map<String, Object>> selectLastSevenDaysRevenue();
}
