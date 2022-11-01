package com.example.SocialLoginService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String username; // 아이디
    private String password; // 비밀번호

    private String role = "ROLE_USER";
    private String provider; // 카카오, 구글, 네이버 인지 구분

    public User() { }

    public User(String username, String password, String provider) {
        this.username = username;
        this.password = password;
        this.provider = provider;
    }

}