package com.toyproject.t4lk.chat.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.toyproject.t4lk.chat.socket.PresenceService.PresenceChange;
import org.junit.jupiter.api.Test;

class PresenceServiceUnitTest {

    @Test
    void enterIncrementsOccupantCount() {
        PresenceService service = new PresenceService();

        PresenceChange first = service.enter(1L, "s1", "유저A");
        assertEquals(1, first.occupantCount());

        PresenceChange second = service.enter(1L, "s2", "유저B");
        assertEquals(2, second.occupantCount());
        assertEquals("유저B", second.displayName());
    }

    @Test
    void leaveDecrementsAndReturnsName() {
        PresenceService service = new PresenceService();
        service.enter(1L, "s1", "유저A");
        service.enter(1L, "s2", "유저B");

        PresenceChange change = service.leave("s1");

        assertEquals(1L, change.roomId());
        assertEquals("유저A", change.displayName());
        assertEquals(1, change.occupantCount());
    }

    @Test
    void leaveUnknownSessionReturnsNull() {
        PresenceService service = new PresenceService();
        assertNull(service.leave("ghost"));
    }

    @Test
    void countsAreScopedPerRoom() {
        PresenceService service = new PresenceService();
        service.enter(1L, "s1", "유저A");
        service.enter(2L, "s2", "유저B");

        assertEquals(1, service.occupantCount(1L));
        assertEquals(1, service.occupantCount(2L));

        service.leave("s1");

        assertEquals(0, service.occupantCount(1L));
        assertEquals(1, service.occupantCount(2L));
    }
}
