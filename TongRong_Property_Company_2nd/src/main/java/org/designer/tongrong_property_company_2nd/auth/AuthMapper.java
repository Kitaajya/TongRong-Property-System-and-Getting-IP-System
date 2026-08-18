package org.designer.tongrong_property_company_2nd.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthMapper {

    private final JdbcTemplate jdbcTemplate;

    public boolean usernameExists(String username) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM LogIn.users WHERE username = ?", username).isEmpty();
    }

    public boolean emailExists(String email) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM LogIn.users WHERE email = ?", email).isEmpty();
    }

    public int insertUser(String username, String email, String passwordHash,
                          String salt, String fullName, String phone,
                          int isOrdinaryUser) {
        return jdbcTemplate.update(
                """
                INSERT INTO LogIn.users (username, email, password_hash, salt, full_name, phone, status, is_ordinary_user, role) \
                VALUES (?, ?, ?, ?, ?, ?, 1, ?, 'user')""",
                username,
                (email == null || email.isBlank()) ? null : email,
                passwordHash,
                salt,
                (fullName == null || fullName.isBlank()) ? null : fullName,
                (phone == null || phone.isBlank()) ? null : phone,
                isOrdinaryUser);
    }

    public List<Map<String, Object>> findByUsername(String username) {
        return jdbcTemplate.queryForList(
                "SELECT id, username, password_hash, salt, full_name, status, is_ordinary_user, role FROM LogIn.users WHERE username = ?",
                username);
    }

    public void updateLoginInfo(int userId, String clientIp, String userAgent) {
        jdbcTemplate.update(
                "UPDATE LogIn.users SET last_login_ip = ?, last_login_time = NOW(), login_count = login_count + 1 WHERE id = ?",
                clientIp, userId);
        jdbcTemplate.update(
                "INSERT INTO LogIn.login_logs (user_id, ip_address, user_agent, login_result) VALUES (?, ?, ?, 1)",
                userId, clientIp, userAgent == null ? null : userAgent.substring(0, Math.min(userAgent.length(), 255)));
    }
}
