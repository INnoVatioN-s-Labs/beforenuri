package com.toyproject.t4lk.session;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceUnitTest {

    @Mock
    private AnonymousSessionRepository anonymousSessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void issueAnonymousSessionBuildsDisplayNameAndToken() {
        when(anonymousSessionRepository.save(any(AnonymousSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnonymousSessionResponse response = sessionService.issueAnonymousSession("203.0.113.42");

        assertNotNull(response.sessionToken());
        assertTrue(response.sessionToken().startsWith("anon-token-"),
                "token: " + response.sessionToken());
        // 닉네임은 "<형용사> <명사>_<IP 앞 두 옥텟>" 형식이어야 한다.
        assertTrue(response.displayName().matches("\\S+ \\S+_203\\.0"),
                "displayName: " + response.displayName());
        verify(anonymousSessionRepository).save(any(AnonymousSession.class));
    }

    @Test
    void issueAnonymousSessionGeneratesUniqueTokens() {
        when(anonymousSessionRepository.save(any(AnonymousSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // count() 기반 채번을 제거했으므로, 동일 IP에서 반복 발급해도 토큰은 매번 유일해야 한다.
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(sessionService.issueAnonymousSession("10.0.0.1").sessionToken());
        }
        assertEquals(1000, tokens.size());
    }

    @Test
    void resolveDisplayNameReturnsStoredNicknameForValidToken() {
        AnonymousSession session = new AnonymousSession("anon-token-honorable-panty-abc", "명예로운 팬티_203.0");
        when(anonymousSessionRepository.findBySessionTokenAndIsDeletedFalse("anon-token-honorable-panty-abc"))
                .thenReturn(Optional.of(session));

        assertEquals("명예로운 팬티_203.0", sessionService.resolveDisplayName("anon-token-honorable-panty-abc"));
    }

    @Test
    void resolveDisplayNameThrowsForUnknownToken() {
        when(anonymousSessionRepository.findBySessionTokenAndIsDeletedFalse("nope"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class, () -> sessionService.resolveDisplayName("nope"));
    }
}
