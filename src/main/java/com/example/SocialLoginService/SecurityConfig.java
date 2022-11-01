package com.example.SocialLoginService;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // AuthenticationFilter 설정정보
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration,authenticationManagerBuilder));
        customAuthenticationFilter.setFilterProcessesUrl("/login");  //  /login 요청시 AuthenticationFilter 가서 처리를 합니다.
        customAuthenticationFilter.setPostOnly(true); // 항상 POST 처리
        customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler); // 성공시 핸들러 설정
        customAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler); // 실패시 핸들러 설정


        http.csrf().disable(); // POST 가능하게 설정

        http.authorizeRequests() // 인증이 된 경우에만 접근을 허용하는데
                .anyRequest()  // 인증된 어떤 요청이든 오던지
                .permitAll()  // 허용하라.
                .and() // 또한
                .oauth2Login() // 소셜로그인을 진행하는데
                .successHandler(authenticationSuccessHandler) //성공하면 커스텀한 핸들러에서 처리한다
                .userInfoEndpoint() // 사용자 정보를 가져올 때
                .userService(customOAuth2UserService); // 커스텀한 서비스에서 정보를 처리한다

        http.addFilter(customAuthenticationFilter); // AuthenticationFilter 추가

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler); // 예외처리 핸들러 설정

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration
                                                        ,AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider); //authenticationProvider 를 설정합니다
        return authenticationConfiguration.getAuthenticationManager(); // AuthenticationManager 정보를 가져옵니다
    }
}
