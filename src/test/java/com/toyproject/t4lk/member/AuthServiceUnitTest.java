package com.toyproject.t4lk.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(memberRepository, refreshTokenRepository, jwtProvider, passwordEncoder, 604800L);
    }

    @Test
    void signupRejectsDuplicateUsername() {
        when(memberRepository.existsByUsernameAndIsDeletedFalse("gildong")).thenReturn(true);
        assertThrows(DuplicateUsernameException.class, () ->
                authService.signup(new SignupRequest("gildong", "password1234", "길동이")));
    }

    @Test
    void signupRejectsDuplicateDisplayName() {
        when(memberRepository.existsByUsernameAndIsDeletedFalse("gildong")).thenReturn(false);
        when(memberRepository.existsByDisplayNameAndIsDeletedFalse("길동이")).thenReturn(true);
        assertThrows(DuplicateDisplayNameException.class, () ->
                authService.signup(new SignupRequest("gildong", "password1234", "길동이")));
    }

    @Test
    void loginWithWrongPasswordThrows() {
        Member member = member(1L, "gildong", "HASH", "길동이");
        when(memberRepository.findByUsernameAndIsDeletedFalse("gildong")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () ->
                authService.login(new LoginRequest("gildong", "wrong")));
    }

    @Test
    void loginSuccessIssuesTokens() {
        Member member = member(1L, "gildong", "HASH", "길동이");
        when(memberRepository.findByUsernameAndIsDeletedFalse("gildong")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password1234", "HASH")).thenReturn(true);
        when(jwtProvider.createAccessToken(1L, "길동이")).thenReturn("atk");

        TokenResponse response = authService.login(new LoginRequest("gildong", "password1234"));

        assertEquals("atk", response.accessToken());
        assertEquals("길동이", response.displayName());
        assertNotNull(response.refreshToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshRejectsExpiredAndRevokes() {
        RefreshToken expired = new RefreshToken("rtk", 1L, Instant.now().minusSeconds(10));
        when(refreshTokenRepository.findByToken("rtk")).thenReturn(Optional.of(expired));

        assertThrows(InvalidTokenException.class, () -> authService.refresh("rtk"));
        verify(refreshTokenRepository).delete(expired); // 회전: 사용된 토큰은 폐기
    }

    @Test
    void refreshRotatesAndIssuesNewTokens() {
        RefreshToken valid = new RefreshToken("rtk", 1L, Instant.now().plusSeconds(1000));
        Member member = member(1L, "gildong", "HASH", "길동이");
        when(refreshTokenRepository.findByToken("rtk")).thenReturn(Optional.of(valid));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(jwtProvider.createAccessToken(1L, "길동이")).thenReturn("atk2");

        TokenResponse response = authService.refresh("rtk");

        assertEquals("atk2", response.accessToken());
        verify(refreshTokenRepository).delete(valid);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    private Member member(Long id, String username, String hash, String displayName) {
        Member member = new Member(username, hash, displayName);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
