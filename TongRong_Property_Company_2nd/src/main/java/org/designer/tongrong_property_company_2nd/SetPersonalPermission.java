package org.designer.tongrong_property_company_2nd;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//个人认证
@Data
@RestController
@RequestMapping("/api/permission")
public class SetPersonalPermission {
    @Autowired
    private JdbcTemplate jdbcTemplate=new JdbcTemplate();
    @GetMapping("/get")
    public List<Map<String,Object>> permission(@RequestParam String name,
                                               @RequestParam String personalNumber){
        return jdbcTemplate.queryForList(
                "SELECT card_no, real_name FROM people WHERE real_name = ? AND card_no = ?",
                name, personalNumber);
    }
}
