package org.designer.tongrong_property_company_2nd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AddWorker {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @PostMapping("/add")
    public int insertWorker(@RequestParam String name,
                            @RequestParam String department, @RequestParam String gender,
                            @RequestParam String work){
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String INSERT_INFORMATION = "insert into log_in(id, name, department, gender, work) " +
                "values(?,?,?,?,?)";
        return jdbcTemplate.update(INSERT_INFORMATION, id, name, department, gender, work);
    }
}
