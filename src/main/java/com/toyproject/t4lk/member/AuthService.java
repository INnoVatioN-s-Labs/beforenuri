package com.toyproject.t4lk.member;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenValiditySeconds;

    public AuthService(
            MemberRepository memberRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            @Value("${app.jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds
    ) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void signup(SignupRequest request) {
        if (memberRepository.existsByUsernameAndIsDeletedFalse(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }
        if (memberRepository.existsByDisplayNameAndIsDeletedFalse(request.displayName())) {
            throw new DuplicateDisplayNameException(request.displayName());
        }
        memberRepository.save(new Member(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.displayName()
        ));
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUsernameAndIsDeletedFalse(request.username())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), member.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return issueTokens(member);
    }

    public TokenResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(InvalidTokenException::new);
        // 회전: 사용된 리프레시 토큰은 즉시 폐기(재사용 방지)
        refreshTokenRepository.delete(stored);
        if (stored.isExpired(Instant.now())) {
            throw new InvalidTokenException();
        }
        Member member = memberRepository.findById(stored.getMemberId())
                .filter(m -> !m.isDeleted())
                .orElseThrow(InvalidTokenException::new);
        return issueTokens(member);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    private TokenResponse issueTokens(Member member) {
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getDisplayName());
        String refreshToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        refreshTokenRepository.save(new RefreshToken(
                refreshToken,
                member.getId(),
                Instant.now().plusSeconds(refreshTokenValiditySeconds)
        ));
        return new TokenResponse(accessToken, refreshToken, member.getDisplayName());
    }
}
