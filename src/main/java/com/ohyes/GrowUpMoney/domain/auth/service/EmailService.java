package com.ohyes.GrowUpMoney.domain.auth.service;

import com.ohyes.GrowUpMoney.domain.auth.exception.EmailSendException;
import com.ohyes.GrowUpMoney.domain.auth.exception.InvalidVerificationCodeException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisTemplate<String,String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void sendEmail(String toEmail, String title, String content) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(content, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("이메일 발송에 실패했습니다.");
        }
    }


    public void verifyCode(String email, String verificationCode) {
        String storedCode = redisTemplate.opsForValue().get(email);

        if (storedCode == null) {
            throw new InvalidVerificationCodeException("인증 코드가 만료되었습니다.");
        }
        if (!storedCode.equals(verificationCode)) {
            throw new InvalidVerificationCodeException("유효하지 않은 인증 코드입니다.");
        }

        redisTemplate.delete(email);
    }


    public String generateCode(){
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);  // 100000~999999
        return code;
    }

    public void sendCodeToEmail(String email) {

        String code = generateCode();  // 6자리 인증코드 생성
        //redis에 저장
        stringRedisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));


        String content = String.format(
                "<h3>이메일 인증 코드</h3>" +
                "<p>인증 코드: <strong>%s</strong></p>" +
                "<p>5분 내에 입력해주세요.</p>",
                code
        );
        sendEmail(email, "[인증번호 : " + code + "] GrowMoney 이메일 인증을 진행해주세요!", content);
    }

}

