package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DatabaseMapper {

    private final JdbcOperations jdbcTemplate;

    public List<Map<String, Object>> showTables() {
        return jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'Tongrong_Company'");
    }

    public List<Map<String, Object>> selectAllFromLogIn() {
        return jdbcTemplate.queryForList("SELECT * FROM log_in");
    }

    public List<Map<String, Object>> selectAllFromExceptionTable() {
        return jdbcTemplate.queryForList("SELECT * FROM exception_table_of_salary");
    }
}
