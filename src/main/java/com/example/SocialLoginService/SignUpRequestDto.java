package com.example.SocialLoginService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private  PasswordEncoder passwordEncoder;

    private String username;
    private String password;

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .role("ROLE_USER")
                .build();
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
