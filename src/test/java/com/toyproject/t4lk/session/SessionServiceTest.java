package com.toyproject.t4lk.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AnonymousSessionRepository anonymousSessionRepository;

    @BeforeEach
    void setUp() {
        anonymousSessionRepository.deleteAll();
    }

    @Test
    void issueAnonymousSessionPersistsSession() {
        var response = sessionService.issueAnonymousSession("203.0.113.42");

        assertNotNull(response.sessionToken());
        assertTrue(response.sessionToken().startsWith("anon-token-"));
        assertTrue(response.displayName().contains("_203.0"));
        assertEquals(1, anonymousSessionRepository.count());
    }

    @Test
    void persistedSessionInheritsBaseEntityFields() {
        sessionService.issueAnonymousSession("203.0.113.42");
        AnonymousSession savedSession = anonymousSessionRepository.findAll().get(0);

        assertNotNull(savedSession.getCreatedAt());
        assertNotNull(savedSession.getUpdatedAt());
        assertFalse(savedSession.isDeleted());
    }
}
