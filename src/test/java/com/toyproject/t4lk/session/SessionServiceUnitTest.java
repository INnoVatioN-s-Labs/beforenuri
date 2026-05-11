package com.toyproject.t4lk.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(anonymousSessionRepository.count()).thenReturn(0L);
        when(anonymousSessionRepository.save(any(AnonymousSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnonymousSessionResponse response = sessionService.issueAnonymousSession();

        assertNotNull(response.sessionToken());
        assertTrue(response.sessionToken().startsWith("anon-token-1-honorable-panty-"));
        assertEquals("명예로운 팬티_192.168", response.displayName());
        verify(anonymousSessionRepository).save(any(AnonymousSession.class));
    }

    @Test
    void issueAnonymousSessionRotatesNicknameByCount() {
        when(anonymousSessionRepository.count()).thenReturn(2L);
        when(anonymousSessionRepository.save(any(AnonymousSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnonymousSessionResponse response = sessionService.issueAnonymousSession();

        assertTrue(response.sessionToken().startsWith("anon-token-3-quiet-modem-"));
        assertEquals("조용한 모뎀_192.168", response.displayName());
    }
}
