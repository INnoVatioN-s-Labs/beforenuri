package com.toyproject.t4lk.chat.socket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * 방별 실시간 접속 세션을 추적한다. (입장/퇴장 알림 및 접속자 수 계산용)
 * DB에 의존하지 않는 인메모리 상태이므로 단위 테스트로 검증한다.
 */
@Service
public class PresenceService {

    // roomId -> (sessionId -> displayName)
    private final Map<Long, Map<String, String>> roomSessions = new ConcurrentHashMap<>();
    // sessionId -> roomId (퇴장 시 어느 방인지 역조회)
    private final Map<String, Long> sessionRoom = new ConcurrentHashMap<>();

    public synchronized PresenceChange enter(Long roomId, String sessionId, String displayName) {
        roomSessions.computeIfAbsent(roomId, key -> new ConcurrentHashMap<>()).put(sessionId, displayName);
        sessionRoom.put(sessionId, roomId);
        return new PresenceChange(roomId, displayName, roomSessions.get(roomId).size());
    }

    public synchronized PresenceChange leave(String sessionId) {
        Long roomId = sessionRoom.remove(sessionId);
        if (roomId == null) {
            return null;
        }
        Map<String, String> sessions = roomSessions.get(roomId);
        String displayName = null;
        int count = 0;
        if (sessions != null) {
            displayName = sessions.remove(sessionId);
            count = sessions.size();
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        return new PresenceChange(roomId, displayName, count);
    }

    public int occupantCount(Long roomId) {
        Map<String, String> sessions = roomSessions.get(roomId);
        return sessions == null ? 0 : sessions.size();
    }

    public record PresenceChange(Long roomId, String displayName, int occupantCount) {
    }
}
