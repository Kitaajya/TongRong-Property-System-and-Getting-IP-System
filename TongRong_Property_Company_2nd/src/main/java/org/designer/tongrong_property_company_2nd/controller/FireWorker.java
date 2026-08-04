package org.designer.tongrong_property_company_2nd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FireWorker {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @GetMapping("/delete")
    public int delete(@RequestParam String id) {
        String DELETE_INFORMATION = "DELETE FROM log_in WHERE id = ?";
        //http://localhost:8080/api/delete?id=EMP105
        return jdbcTemplate.update(DELETE_INFORMATION, id);
    }
}
