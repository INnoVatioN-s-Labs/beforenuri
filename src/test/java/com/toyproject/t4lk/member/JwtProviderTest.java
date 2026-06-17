package com.toyproject.t4lk.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider(
            "test-secret-test-secret-test-secret-test-secret-0123456789", 3600);

    @Test
    void createAndResolveAccessToken() {
        String token = jwtProvider.createAccessToken(1L, "길동이");
        assertEquals(Optional.of("길동이"), jwtProvider.resolveDisplayName(token));
    }

    @Test
    void resolveInvalidTokenReturnsEmpty() {
        assertTrue(jwtProvider.resolveDisplayName("not-a-jwt").isEmpty());
        assertTrue(jwtProvider.resolveDisplayName("anon-token-honorable-cat-xyz").isEmpty());
    }
}
