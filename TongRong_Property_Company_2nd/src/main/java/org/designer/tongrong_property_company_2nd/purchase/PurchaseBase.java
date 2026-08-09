package org.designer.tongrong_property_company_2nd.purchase;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@RestController
@RequestMapping("/api/purchase")
public class PurchaseBase {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/travel")
    public List<Map<String,Object>> travelProducts(){
        log.info("查看商品库");
        return jdbcTemplate.queryForList("SELECT * FROM PurchaseBase.products");
    }
    ConcurrentHashMap<String,Object> concurrentHashMap;
}
