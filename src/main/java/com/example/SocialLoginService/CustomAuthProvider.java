package com.example.SocialLoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();


        System.out.println("CustomAuthProvider username = " + username);
        System.out.println("CustomAuthProvider password = " + password);


        CustomIntegratedLogin userDetails = (CustomIntegratedLogin) customUserDetailsService.loadUserByUsername(username);

        System.out.println("userDetails.getPassword() = " + userDetails.getPassword());
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());

        // PW 검사
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Provider - authenticate() : 비밀번호가 일치하지 않습니다.");
        }

        System.out.println("ok");

//        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
