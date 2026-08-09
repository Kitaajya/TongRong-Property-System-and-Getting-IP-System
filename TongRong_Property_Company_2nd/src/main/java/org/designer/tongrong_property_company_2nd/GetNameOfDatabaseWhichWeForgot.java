package org.designer.tongrong_property_company_2nd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class GetNameOfDatabaseWhichWeForgot {
    @Autowired
    private JdbcOperations jdbcTemplate;
    @RequestMapping("/tables")
    public List<Map<String, Object>> tables() {
        //数据库名"exception_table_of_salary"&"log_in"
        return jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'Tongrong_Company'");
    }
    @GetMapping("/travel_log_in")
    public List<Map<String,Object>> travelTable(){
        return jdbcTemplate.queryForList("SELECT * FROM log_in");
    }
    @GetMapping("travel_exception")
    public List<Map<String,Object>> travelExceptionTable(){
        return jdbcTemplate.queryForList("SELECT * FROM exception_table_of_salary");
    }
}
