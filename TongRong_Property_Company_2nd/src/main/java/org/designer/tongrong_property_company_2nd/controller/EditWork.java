package org.designer.tongrong_property_company_2nd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EditWork {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @PostMapping("/edit")
    public int edit(@RequestParam String id,
                    @RequestParam String name,
                    @RequestParam String department, @RequestParam String gender,
                    @RequestParam String work) {
        String EDIT_INFORMATION = "UPDATE log_in SET name = ?, department = ?, gender = ?, work = ? WHERE id = ?";
        /**
         * "http://localhost:8080/api/edit?
         * id=e770f25b40d04e8aa93a&name=
         * %E5%BC%A0123&department=%E5%B7%A5%E7%A8%8B%E9%83%A8&gender=
         * %E7%94%B7&work=%E7%BB%B4%E4%BF%AE"
         * **/
        return jdbcTemplate.update(EDIT_INFORMATION, name, department, gender, work, id);
    }
}
