package com.toyproject.t4lk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class WebConfigTest {

    @Value("${app.allowed-origins}")
    private String[] allowedOrigins;

    @Test
    void corsConfigUsesProductionWebOrigin() {
        assertTrue(Arrays.asList(allowedOrigins).contains("https://beforenuri-web.vercel.app"));
    }
}
