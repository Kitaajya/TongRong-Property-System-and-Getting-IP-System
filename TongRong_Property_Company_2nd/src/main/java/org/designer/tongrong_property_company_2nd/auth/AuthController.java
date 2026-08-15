package org.designer.tongrong_property_company_2nd.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * 登录系统：注册 / 登录 / 登出 / 登录状态查询
 * 未登录时，拦截器会拦截所有 /api/** 请求，查询与购买功能均无法使用
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_USER_KEY = "loginUser";
    private static final String DEFAULT_SALT = "fixed_salt";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 与数据库脚本一致的密码哈希：SHA2(明文密码 + salt, 256) 的小写十六进制
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 注册账号
    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String fullName,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) Boolean isOrdinaryUser) {
        if (username == null || username.isBlank() || password == null || password.length() < 6)
            return Map.of("success", false, "message", "用户名不能为空，密码长度至少6位");

        List<Map<String, Object>> exists =
                jdbcTemplate.queryForList("SELECT id FROM LogIn.users WHERE username = ?", username);
        if (!exists.isEmpty())
            return Map.of("success", false, "message", "用户名已存在");

        if (email != null && !email.isBlank()) {
            List<Map<String, Object>> emailExists =
                    jdbcTemplate.queryForList("SELECT id FROM LogIn.users WHERE email = ?", email);
            if (!emailExists.isEmpty())
                return Map.of("success", false, "message", "该邮箱已被注册");
        }

        try {
            int isMerchant = (isOrdinaryUser != null && isOrdinaryUser) ? 1 : 0;
            int rows = jdbcTemplate.update(
                    "INSERT INTO LogIn.users (username, email, password_hash, salt, full_name, phone, status, is_ordinary_user) " +
                            "VALUES (?, ?, ?, ?, ?, ?, 1, ?)",
                    username,
                    (email == null || email.isBlank()) ? null : email,
                    sha256Hex(password + DEFAULT_SALT),
                    DEFAULT_SALT,
                    (fullName == null || fullName.isBlank()) ? null : fullName,
                    (phone == null || phone.isBlank()) ? null : phone,
                    isMerchant);

            if (rows > 0) {
                log.info("新用户注册成功：{}", username);
                return Map.of("success", true, "message", "注册成功，请登录");
            }
        } catch (DuplicateKeyException e) {
            log.warn("注册失败：用户名或邮箱已存在 {}", username);
            return Map.of("success", false, "message", "用户名或邮箱已被注册");
        }
        return Map.of("success", false, "message", "注册失败");
    }

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(HttpServletRequest request,
                                     @RequestParam String username,
                                     @RequestParam String password) {
        List<Map<String, Object>> userList = jdbcTemplate.queryForList(
                "SELECT id, username, password_hash, salt, full_name, status, is_ordinary_user FROM LogIn.users WHERE username = ?",
                username);

        if (userList.isEmpty()) {
            log.warn("登录失败：用户不存在 {}", username);
            return Map.of("success", false, "message", "用户不存在");
        }

        Map<String, Object> user = userList.get(0);
        int status = ((Number) user.get("status")).intValue();
        if (status == 0)
            return Map.of("success", false, "message", "账号已被禁用");

        String salt = String.valueOf(user.get("salt"));
        String storedHash = String.valueOf(user.get("password_hash"));
        if (!storedHash.equalsIgnoreCase(sha256Hex(password + salt))) {
            log.warn("登录失败：密码错误 {}", username);
            return Map.of("success", false, "message", "密码错误");
        }

        // 写入会话
        HttpSession session = request.getSession(true);
        Object merchantFlag = user.get("is_ordinary_user");
        boolean isOrdinaryUser = (merchantFlag != null) && ((Number) merchantFlag).intValue() == 1;
        session.setAttribute(SESSION_USER_KEY, Map.of(
                "id", user.get("id"),
                "username", user.get("username"),
                "fullName", user.get("full_name"),
                "isOrdinaryUser", isOrdinaryUser));

        int userId = ((Number) user.get("id")).intValue();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        // 更新登录信息
        jdbcTemplate.update(
                "UPDATE LogIn.users SET last_login_ip = ?, last_login_time = NOW(), login_count = login_count + 1 WHERE id = ?",
                clientIp, userId);
        // 记录登录日志
        jdbcTemplate.update(
                "INSERT INTO LogIn.login_logs (user_id, ip_address, user_agent, login_result) VALUES (?, ?, ?, 1)",
                userId, clientIp, userAgent == null ? null : userAgent.substring(0, Math.min(userAgent.length(), 255)));

        log.info("用户登录成功：{}", username);
        return Map.of("success", true, "message", "登录成功",
                "username", user.get("username"),
                "fullName", user.get("full_name"),
                "isOrdinaryUser", isOrdinaryUser);
    }

    // 登出
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return Map.of("success", true, "message", "已退出登录");
        }
        return Map.of("success", true, "message", "未登录");
    }

    // 登录状态查询
    @GetMapping("/status")
    public Map<String, Object> status(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object loginUser = session.getAttribute(SESSION_USER_KEY);
            if (loginUser instanceof Map<?, ?> m) {
                return Map.of("loggedIn", true, "user", m);
            }
        }
        return Map.of("loggedIn", false);
    }
}
