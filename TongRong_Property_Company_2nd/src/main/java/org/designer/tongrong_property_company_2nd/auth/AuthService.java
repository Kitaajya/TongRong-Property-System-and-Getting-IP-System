package org.designer.tongrong_property_company_2nd.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String SESSION_USER_KEY = "loginUser";

    @Value("${auth.salt:fixed_salt}")
    private String defaultSalt;

    private final AuthMapper authMapper;
    private final EmailCodeService emailCodeService;

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

    public Map<String, Object> register(String username, String password,
                                         String email, String emailCode,
                                         String fullName, String phone,
                                         Boolean isOrdinaryUser) {
        if (username == null || username.isBlank() || password == null || password.length() < 6)
            return Map.of("success", false, "message", "用户名不能为空，密码长度至少6位");

        if (authMapper.usernameExists(username))
            return Map.of("success", false, "message", "用户名已存在");

        if (email != null && !email.isBlank()) {
            if (authMapper.emailExists(email.trim()))
                return Map.of("success", false, "message", "该邮箱已被注册");

            String verifyMsg = emailCodeService.verify(email, emailCode);
            if (verifyMsg != null)
                return Map.of("success", false, "message", verifyMsg);
        }

        try {
            int isMerchant = (isOrdinaryUser != null && isOrdinaryUser) ? 1 : 0;
            int rows = authMapper.insertUser(username, email,
                    sha256Hex(password + defaultSalt), defaultSalt,
                    fullName, phone, isMerchant);
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

    public Map<String, Object> login(HttpServletRequest request, String username, String password) {
        List<Map<String, Object>> userList = authMapper.findByUsername(username);

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

        HttpSession session = request.getSession(true);
        Object merchantFlag = user.get("is_ordinary_user");
        boolean isOrdinaryUser = (merchantFlag != null) && ((Number) merchantFlag).intValue() == 1;
        Object roleObj = user.get("role");
        String role = (roleObj == null || String.valueOf(roleObj).isBlank())
                ? (isOrdinaryUser ? "merchant" : "user")
                : String.valueOf(roleObj);
        session.setAttribute(SESSION_USER_KEY, Map.of(
                "id", user.get("id"),
                "username", user.get("username"),
                "fullName", user.get("full_name"),
                "isOrdinaryUser", isOrdinaryUser,
                "role", role));

        int userId = ((Number) user.get("id")).intValue();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        authMapper.updateLoginInfo(userId, clientIp, userAgent);

        log.info("用户登录成功：{} 角色:{}", username, role);
        return Map.of("success", true, "message", "登录成功",
                "username", user.get("username"),
                "fullName", user.get("full_name"),
                "isOrdinaryUser", isOrdinaryUser,
                "role", role);
    }
}
