package dev.aniketkadam.annapoorna.email;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationRequest {
    private String to;
    private EmailTemplateName emailTemplate;
    private String verificationCode;
    private String subject;
}
