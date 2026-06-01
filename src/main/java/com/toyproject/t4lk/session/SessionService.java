package com.toyproject.t4lk.session;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
        // count() 기반 순번은 동시 요청 시 같은 값을 읽어(read-modify-write 레이스) 토큰 충돌을
        // 유발하므로, 닉네임은 랜덤 인덱스로 고르고 토큰 유일성은 전체 UUID로 보장한다.
        int index = ThreadLocalRandom.current().nextInt(NOUNS.size());
        String adjective = ADJECTIVES.get(index);
        String noun = NOUNS.get(index);
        String ipSuffix = ClientIpResolver.toDisplaySuffix(clientIp);
        String displayName = adjective + " " + noun + "_" + ipSuffix;
        String token = "anon-token-" + ADJECTIVE_KEYS.get(index) + "-" + NOUN_KEYS.get(index)
                + "-" + UUID.randomUUID();

        AnonymousSession savedSession = anonymousSessionRepository.save(new AnonymousSession(token, displayName));
        return new AnonymousSessionResponse(savedSession.getSessionToken(), savedSession.getDisplayName());
    }
}
