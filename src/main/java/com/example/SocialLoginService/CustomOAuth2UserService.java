package com.example.SocialLoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        // 어떤 소셜로그인을 이용하는지 구분 하기위해 쓰임.
        // ex) registrationId = "naver", registrationId = "google" 등등
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();


        // OAuth2 로그인 시 키 값이 된다.
        // 구글은 키 값이 "sub"이고, 네이버는 "response"이고, 카카오는 "id"이다.
        // 각각 다르므로 이렇게 따로 변수로 받아서 넣어줘야함.
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2 로그인을 통해 가져온 OAuth2User의 attribute를 담아주는 of 메소드.
        // oAuth2User.getAttributes() 에는 다음 값이 담긴다.
        // 구글 예시) { sub=1231344534523565757, name=홍길동, given_name=길동, family_name=홍, picture=https://xxx, email=xxx@gmail.com, email_verified=true, locale=ko}
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String encode = passwordEncoder.encode(UUID.randomUUID().toString());

        // user 예시) username=google_1231344534523565757 , password = $2a$10$nlkgA6oUE.7KpHSO6tDDpOBth4PICf1DeQiHQ2qbbaA8o3s1osGvG
        User user = new User(registrationId+"_"+attributes.getUsername(), encode ,registrationId);


        User loadOrSaveUser = loadOrSave(user);

        return new CustomIntegratedLogin(loadOrSaveUser,attributes);

    }

    // 이미 저장된 유저라면 load, 아니면 save
    private User loadOrSave(User user) {
        Optional<User> loadUser = userRepository.findByUsername(user.getUsername());

        if (loadUser.isEmpty()){ // 소셜로그인을 시도한 아이디가 없다면 저장하고
            return userRepository.save(user);
        } else { // 소셜로그인을 시도한 아이디가 있다면 가져온다
            return loadUser.get();
        }
    }
}