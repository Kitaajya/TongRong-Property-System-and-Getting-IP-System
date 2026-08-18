package org.designer.tongrong_property_company_2nd.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String SESSION_USER_KEY = "loginUser";

    private final AuthService authService;
    private final EmailCodeService emailCodeService;

    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestParam String email) {
        return emailCodeService.sendCode(email);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String emailCode,
                                        @RequestParam(required = false) String fullName,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) Boolean isOrdinaryUser) {
        return authService.register(username, password, email, emailCode, fullName, phone, isOrdinaryUser);
    }

    @PostMapping("/login")
    public Map<String, Object> login(HttpServletRequest request,
                                     @RequestParam String username,
                                     @RequestParam String password) {
        return authService.login(request, username, password);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return Map.of("success", true, "message", "已退出登录");
        }
        return Map.of("success", true, "message", "未登录");
    }

    @GetMapping("/status")
    public Map<String, Object> status(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object loginUser = session.getAttribute(SESSION_USER_KEY);
            if (loginUser instanceof Map<?, ?> m)
                return Map.of("loggedIn", true, "user", m);
        }
        return Map.of("loggedIn", false);
    }
}
