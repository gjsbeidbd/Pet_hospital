package edu.mycc.xhd.pethospitalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.mycc.xhd.pethospitalproject.entity.BillingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BillingItemMapper extends BaseMapper<BillingItem> {
    
    @Select("SELECT * FROM billing_items WHERE billing_id = #{billingId} ORDER BY created_at DESC")
    List<BillingItem> selectByBillingId(@Param("billingId") Long billingId);
}
