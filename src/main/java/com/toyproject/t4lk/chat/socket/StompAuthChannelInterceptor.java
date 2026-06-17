package com.toyproject.t4lk.chat.socket;

import com.toyproject.t4lk.session.IdentityResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * STOMP CONNECT 시점에 sessionToken 헤더를 검증하고, 조회된 닉네임을 세션 Principal로
 * 고정한다. 이후 SEND 프레임의 발신자 이름은 클라이언트 입력이 아니라 이 Principal에서
 * 결정되므로 닉네임 사칭을 막을 수 있다. 토큰이 없거나 유효하지 않으면 연결을 거부한다.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    public static final String SESSION_TOKEN_HEADER = "sessionToken";

    private final IdentityResolver identityResolver;

    public StompAuthChannelInterceptor(IdentityResolver identityResolver) {
        this.identityResolver = identityResolver;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader(SESSION_TOKEN_HEADER);
            String displayName = identityResolver.resolveDisplayName(token);
            accessor.setUser(() -> displayName);
        }
        return message;
    }
}
