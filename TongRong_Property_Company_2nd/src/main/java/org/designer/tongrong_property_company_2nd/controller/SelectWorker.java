package org.designer.tongrong_property_company_2nd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class SelectWorker {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @RequestMapping("/select")
    public List<Map<String,Object>> testJDBC(){
        String SELECT_INFORMATION="select * from log_in";
        return jdbcTemplate.queryForList(SELECT_INFORMATION);
    }
}
