package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PermissionMapper {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findPerson(String name, String cardNo) {
        return jdbcTemplate.queryForList(
                "SELECT card_no, real_name FROM people WHERE real_name = ? AND card_no = ?",
                name, cardNo);
    }
}
