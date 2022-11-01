package com.example.SocialLoginService;

import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes; // ex) { sub=1231344534523565757, name=홍길동, given_name=길동, family_name=홍, picture=https://xxx, email=xxx@gmail.com, email_verified=true, locale=ko}
    private String nameAttributeKey; // ex) { google="sub", kakao="id", naver="response" }
    private String name;  // ex) 홍길동
    private String email;  // ex) test@gmail.com
    private String username; // ex) 1231344534523565757


    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,String username) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.username=username;

    }


    // 해당 로그인인 서비스가 kakao 인지 google 인지 구분하여, 알맞게 매핑을 해주도록 합니다.
    // registrationId는 소셜로그인을 시도한 서비스 명("google","kakao","naver"..)이 되고,
    // userNameAttributeName은 해당 서비스의 map의 키 값이 되는 값이 됩니다. { google="sub", kakao="id", naver="response" }
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equals("kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        } else if (registrationId.equals("naver")) {
            return ofNaver(userNameAttributeName,attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    // 카카오 예시
    // attributes = {
    //          id=3498023493,     이 부분을 가져온다(username)
    //          connected_at=2022-07-17T12:40:28Z,
    //          properties={nickname=xxx},
    //          kakao_account={
    //                   profile_nickname_needs_agreement=false,
    //                   profile={nickname=xxx},    이 부분을 가져온다
    //                   has_email=true,
    //                   email_needs_agreement=false,
    //                   is_email_valid=true,
    //                   is_email_verified=true,
    //                   email=xxx@nate.com   이 부분을 가져온다
    //                }
    //          }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");  // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");   // 마찬가지로 profile(nickname 등) 정보가 담긴 값을 꺼낸다.
        String username = attributes.get(userNameAttributeName).toString();
        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) profile.get("nickname"),
                (String) kakao_account.get("email"),
                username
               );
    }

    // 네이버 예시
    // attributes = {
    //          resultcode=00,
    //          message=success,
    //          response= {
    //               id=lkdslsdkfjsdf-sdf98334twfdgdfv,  이 부분을 가져온다
    //               nickname=xx,
    //               email=xx@naver.com,    이 부분을 가져온다
    //               name=xxx     이 부분을 가져온다
    //               }
    //         }
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");    // 네이버에서 받은 데이터에서 프로필 정보다 담긴 response 값을 꺼낸다.
        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) response.get("name"),
                (String) response.get("email"),
                (String)response.get("id")
               );
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        String username = attributes.get(userNameAttributeName).toString();
        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) attributes.get("name"),
                (String) attributes.get("email"),username
               );
    }

}
