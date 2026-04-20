package team2.stk.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("onboarding@resend.dev");
            message.setTo(to);
            message.setSubject("재고관리 시스템 이메일 인증");
            message.setText("인증 코드: " + code + "\n\n이 코드는 3분 후에 만료됩니다.");

            javaMailSender.send(message);
            log.info("이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }
}
