package com.toyproject.t4lk.session;

import com.toyproject.t4lk.member.JwtProvider;
import org.springframework.stereotype.Component;

/**
 * 요청/연결의 토큰으로 표시 닉네임을 해석한다.
 * 회원 액세스 토큰(JWT)이면 토큰 claim의 고정 닉네임을, 아니면 익명 세션 토큰으로 해석한다.
 */
@Component
public class IdentityResolver {

    private final JwtProvider jwtProvider;
    private final SessionService sessionService;

    public IdentityResolver(JwtProvider jwtProvider, SessionService sessionService) {
        this.jwtProvider = jwtProvider;
        this.sessionService = sessionService;
    }

    public String resolveDisplayName(String token) {
        return jwtProvider.resolveDisplayName(token)
                .orElseGet(() -> sessionService.resolveDisplayName(token));
    }
}
