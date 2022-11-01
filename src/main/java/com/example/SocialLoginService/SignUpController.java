package com.example.SocialLoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/signup")
    public void signup(@RequestBody SignUpRequestDto dto) {
        dto.encodePassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(dto.toEntity());

    }
}
