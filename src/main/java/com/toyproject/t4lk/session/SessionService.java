package com.toyproject.t4lk.session;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {

    private static final List<String> ADJECTIVES = List.of("명예로운", "용감한", "조용한", "날카로운");
    private static final List<String> ADJECTIVE_KEYS = List.of("honorable", "brave", "quiet", "sharp");
    private static final List<String> NOUNS = List.of("팬티", "고양이", "모뎀", "사용자");
    private static final List<String> NOUN_KEYS = List.of("panty", "cat", "modem", "user");
    private final AnonymousSessionRepository anonymousSessionRepository;

    public SessionService(AnonymousSessionRepository anonymousSessionRepository) {
        this.anonymousSessionRepository = anonymousSessionRepository;
    }

    public AnonymousSessionResponse issueAnonymousSession(String clientIp) {
        long current = anonymousSessionRepository.count() + 1;
        int index = (int) ((current - 1) % NOUNS.size());
        String adjective = ADJECTIVES.get(index);
        String noun = NOUNS.get(index);
        String ipSuffix = ClientIpResolver.toDisplaySuffix(clientIp);
        String displayName = adjective + " " + noun + "_" + ipSuffix;
        String token = "anon-token-" + current + "-" + ADJECTIVE_KEYS.get(index) + "-" + NOUN_KEYS.get(index)
                + "-" + UUID.randomUUID().toString().substring(0, 8);

        AnonymousSession savedSession = anonymousSessionRepository.save(new AnonymousSession(token, displayName));
        return new AnonymousSessionResponse(savedSession.getSessionToken(), savedSession.getDisplayName());
    }
}
