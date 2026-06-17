package com.toyproject.t4lk.session;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {

    private static final List<String> ADJECTIVES = List.of(
            "명예로운", "용감한", "조용한", "날카로운", "쓸쓸한", "엉뚱한", "수상한", "변덕스러운",
            "우아한", "까칠한", "전설적인", "빛나는", "심오한", "게으른", "부지런한", "레트로한",
            "8비트", "삐삐치는", "다이얼업하는", "오버클럭된", "버퍼링중인", "접속불량인", "야간모드의", "심야의"
    );
    private static final List<String> NOUNS = List.of(
            "팬티", "고양이", "모뎀", "사용자", "너구리", "두꺼비", "도토리", "햄스터",
            "감자", "고구마", "플로피디스크", "카세트테이프", "삐삐", "워크맨", "다마고치", "컵라면",
            "붕어빵", "떡볶이", "통신원", "시삽", "키보드워리어", "네티즌", "오리", "흑우"
    );
    private final AnonymousSessionRepository anonymousSessionRepository;

    public SessionService(AnonymousSessionRepository anonymousSessionRepository) {
        this.anonymousSessionRepository = anonymousSessionRepository;
    }

    public AnonymousSessionResponse issueAnonymousSession(String clientIp) {
        // 형용사/명사를 독립적으로 랜덤 선택해 조합 다양성을 높인다(24 x 24 = 576가지).
        // 토큰 유일성은 전체 UUID로 보장한다.
        int adjectiveIndex = ThreadLocalRandom.current().nextInt(ADJECTIVES.size());
        int nounIndex = ThreadLocalRandom.current().nextInt(NOUNS.size());
        String ipSuffix = ClientIpResolver.toDisplaySuffix(clientIp);
        String displayName = ADJECTIVES.get(adjectiveIndex) + " " + NOUNS.get(nounIndex) + "_" + ipSuffix;
        String token = "anon-token-" + UUID.randomUUID();

        AnonymousSession savedSession = anonymousSessionRepository.save(new AnonymousSession(token, displayName));
        return new AnonymousSessionResponse(savedSession.getSessionToken(), savedSession.getDisplayName());
    }

    @Transactional(readOnly = true)
    public String resolveDisplayName(String sessionToken) {
        return anonymousSessionRepository.findBySessionTokenAndIsDeletedFalse(sessionToken)
                .map(AnonymousSession::getDisplayName)
                .orElseThrow(InvalidSessionException::new);
    }
}
