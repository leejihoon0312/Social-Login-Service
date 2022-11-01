package com.example.SocialLoginService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.SocialLoginService.JwtConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        //로그인 성공시 다음을 수행
        // 소셜로그인 유저와 일반 로그인 유저 모두 여기서 처리합니다
        CustomIntegratedLogin user = (CustomIntegratedLogin) authentication.getPrincipal();
        // 소셜로그인 ex) user.getUsername() = google_1231344534523565757, user.getPassword() = $2a$10$nlkgA6oUE.7KpHSO6tDDpOBth4PICf1DeQiHQ2qbbaA8o3s1osGvG
        //일반 로그인 ex) user.getUsername() = 로그인 시도 아이디 ,user.getPassword() = $sdf34$dfgs$Rr.7KpHSO6tDDpOBth4PICf1DeQiHdgerteshtwefs435


        String accessToken = JWT.create() // JWT 생성을 시도하는데
                .withSubject(user.getUsername()) // JWT 의 이름(제목)을 정한다.
                .withExpiresAt(new Date(System.currentTimeMillis() + AT_EXP_TIME)) // JWT 의 만료시간을 지정한다. 설정하지 않으면 기본적으로 무한지속 된다.
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) //사용자 지정 클레임. 커스텀이 가능하다
                .withIssuedAt(new Date(System.currentTimeMillis())) // 언제 생성 되었는지 기록해둔다.
                .sign(Algorithm.HMAC256(JWT_SECRET)); // 어떤 해싱 알고리즘으로 해시를 하는지, 어떤 시크릿키를 사용하는지 결정한다.

        // Access Token 을 프론트 단에 Response Header로 전달
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setHeader(AT_HEADER, accessToken);


        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AT_HEADER, accessToken);
        new ObjectMapper().writeValue(response.getWriter(), responseMap);
    }
}
