package com.toyproject.t4lk.session;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final List<String> ADJECTIVES = List.of("명예로운", "용감한", "조용한", "날카로운");
    private static final List<String> ADJECTIVE_KEYS = List.of("honorable", "brave", "quiet", "sharp");
    private static final List<String> NOUNS = List.of("팬티", "고양이", "모뎀", "사용자");
    private static final List<String> NOUN_KEYS = List.of("panty", "cat", "modem", "user");
    private final AtomicLong sequence = new AtomicLong(1);

    public AnonymousSessionResponse issueAnonymousSession() {
        long current = sequence.getAndIncrement();
        int index = (int) ((current - 1) % ADJECTIVES.size());
        String adjective = ADJECTIVES.get(index);
        String noun = NOUNS.get(index);
        String displayName = adjective + " " + noun + "_192.168";
        String token = "anon-token-" + current + "-" + ADJECTIVE_KEYS.get(index) + "-" + NOUN_KEYS.get(index);
        return new AnonymousSessionResponse(token, displayName);
    }
}
