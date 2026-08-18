package org.designer.tongrong_property_company_2nd.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private record CodeEntry(String code, LocalDateTime expireAt, LocalDateTime lastSendAt) {}

    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Value("${mail.code.expire-minutes:5}")
    private int expireMinutes;

    @Value("${mail.code.resend-seconds:60}")
    private long resendSeconds;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    private final JavaMailSender mailSender;

    public Map<String, Object> sendCode(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches())
            return Map.of("success", false, "message", "邮箱格式不正确");
        email = email.trim().toLowerCase();

        LocalDateTime now = LocalDateTime.now();
        CodeEntry old = codeStore.get(email);
        if (old != null && old.lastSendAt().plusSeconds(resendSeconds).isAfter(now))
            return Map.of("success", false, "message", "发送太频繁，请稍后再试");

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codeStore.put(email, new CodeEntry(code, now.plusMinutes(expireMinutes), now));

        try {
            sendMail(email, code);
            log.info("已向 {} 发送邮箱验证码", email);
            return Map.of("success", true, "message", "验证码已发送到 " + email);
        } catch (Exception e) {
            codeStore.remove(email);
            log.error("发送验证码失败：{}", email, e);
            return Map.of("success", false, "message", "邮件发送失败：" + e.getMessage());
        }
    }

    public String verify(String email, String code) {
        if (email == null || code == null) return "邮箱或验证码不能为空";
        email = email.trim().toLowerCase();
        CodeEntry entry = codeStore.remove(email);
        if (entry == null) return "请先获取验证码";
        if (entry.expireAt().isBefore(LocalDateTime.now())) return "验证码已过期，请重新获取";
        if (!entry.code().equals(code.trim())) return "验证码错误";
        return null;
    }

    private void sendMail(String to, String code) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        if (senderEmail != null && !senderEmail.isBlank())
            helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("【通融物业】邮箱验证码");
        helper.setText(
                "<div style='font-family:微软雅黑,sans-serif;padding:24px;'>"
                        + "<h3>邮箱验证码</h3>"
                        + "<p>您的验证码是：<b style='font-size:22px;color:#d9534f;'>" + code + "</b></p>"
                        + "<p>有效期 " + expireMinutes + " 分钟，请勿泄露给他人。</p>"
                        + "<p style='color:#888;font-size:12px;'>此邮件由系统自动发送，请勿回复。</p>"
                        + "</div>",
                true);
        mailSender.send(message);
    }
}
