package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class WorkerMapper {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL =
            "INSERT INTO log_in(id, name, department, gender, work) VALUES(?,?,?,?,?)";

    private static final String SELECT_ALL_SQL = "SELECT * FROM log_in";

    private static final String UPDATE_SQL =
            "UPDATE log_in SET name = ?, department = ?, gender = ?, work = ? WHERE id = ?";

    private static final String DELETE_SQL = "DELETE FROM log_in WHERE id = ?";

    public int insert(String id, String name, String department, String gender, String work) {
        return jdbcTemplate.update(INSERT_SQL, id, name, department, gender, work);
    }

    public List<Map<String, Object>> selectAll() {
        return jdbcTemplate.queryForList(SELECT_ALL_SQL);
    }

    public int update(String id, String name, String department, String gender, String work) {
        return jdbcTemplate.update(UPDATE_SQL, name, department, gender, work, id);
    }

    public int deleteById(String id) {
        return jdbcTemplate.update(DELETE_SQL, id);
    }
}
