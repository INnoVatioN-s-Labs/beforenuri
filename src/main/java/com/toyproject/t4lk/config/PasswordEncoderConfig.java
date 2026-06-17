package com.toyproject.t4lk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 해시용 BCrypt 인코더. (Spring Security 필터 체인은 사용하지 않고
 * spring-security-crypto의 BCrypt만 차용한다. BCrypt는 salt를 매번 생성해 해시에 함께 저장한다.)
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
