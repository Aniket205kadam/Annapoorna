package dev.aniketkadam.annapoorna.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${application.mail.from}")
    private String fromEmail;
    @Value("${application.mail.reply-to}")
    private String replyToEmail;

    @Async
    public void sendEmail(EmailVerificationRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", request.getTo());
        properties.put("verificationCode", request.getVerificationCode());

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(fromEmail);
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setSentDate(new Date(System.currentTimeMillis()));
        helper.setReplyTo(replyToEmail);

        mimeMessage.addHeader("X-Custom-Header", request.getVerificationCode() + " is your Annapoorna code");
        mimeMessage.setHeader("X-No-Reply", "true");

        String template = templateEngine.process(request.getEmailTemplate().getName(), context);
        helper.setText(template, true);

        // Add App logo on email
        helper.addInline("logo", new ClassPathResource("static/images/Annapoorna-logo.jpg"));

        mailSender.send(mimeMessage);
    }
}
