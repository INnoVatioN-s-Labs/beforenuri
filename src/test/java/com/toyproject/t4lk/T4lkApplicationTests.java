package com.toyproject.t4lk;

import com.toyproject.t4lk.room.RoomController;
import com.toyproject.t4lk.room.RoomNotFoundException;
import com.toyproject.t4lk.session.SessionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class T4lkApplicationTests {

    @Autowired
    private HealthController healthController;

    @Autowired
    private RoomController roomController;

    @Autowired
    private SessionController sessionController;

    @Test
    void contextLoads() {
    }

    @Test
    void healthReturnsOk() throws Exception {
        assertEquals("ok", healthController.health().get("status"));
    }

    @Test
    void getRoomsReturnsList() {
        assertFalse(roomController.getRooms().isEmpty());
        assertEquals("자유 대화실", roomController.getRooms().get(0).title());
    }

    @Test
    void getRoomThrowsWhenMissing() {
        assertThrows(RoomNotFoundException.class, () -> roomController.getRoom(999L));
    }

    @Test
    void issueAnonymousSessionReturnsTokenAndDisplayName() {
        var response = sessionController.issueAnonymousSession();

        assertNotNull(response.sessionToken());
        assertTrue(response.sessionToken().startsWith("anon-token-"));
        assertTrue(response.displayName().contains("_192.168"));
    }

}
