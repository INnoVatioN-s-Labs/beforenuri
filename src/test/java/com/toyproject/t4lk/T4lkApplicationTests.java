package com.toyproject.t4lk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class T4lkApplicationTests {

    @Autowired
    private HealthController healthController;

    @Test
    void contextLoads() {
    }

    @Test
    void healthReturnsOk() {
        assertEquals("ok", healthController.health().get("status"));
    }
}
