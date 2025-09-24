package dev.aniketkadam.annapoorna.email;

import dev.aniketkadam.annapoorna.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Async
    public void sendRestPasswordEmail(@NotEmpty String fullName, @NotEmpty String email, @NotEmpty String passwordResetLink) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", fullName);
        properties.put("passwordResetLink", passwordResetLink);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject(fullName + ", we've made it easy to get back on Annapoorna");
        helper.setSentDate(new Date(System.currentTimeMillis()));
        helper.setReplyTo(replyToEmail);

        mimeMessage.addHeader("X-Custom-Header", fullName + ", we've made it easy to get back on Annapoorna");
        mimeMessage.setHeader("X-No-Reply", "true");

        String template = templateEngine.process(EmailTemplateName.FORGOT_PASSWORD.getName(), context);
        helper.setText(template, true);

        // Add App logo on email
        helper.addInline("logo", new ClassPathResource("static/images/Annapoorna-logo.jpg"));

        mailSender.send(mimeMessage);
    }
}
