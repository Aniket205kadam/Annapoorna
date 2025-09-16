package dev.aniketkadam.annapoorna.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    EMAIL_VERIFICATION("email_verification"),
    FORGOT_PASSWORD("forgot_password")

    ;
    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
