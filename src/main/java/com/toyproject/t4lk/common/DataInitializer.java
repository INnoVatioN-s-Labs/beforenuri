package com.toyproject.t4lk.common;

import java.util.List;

import com.toyproject.t4lk.chat.ChatMessage;
import com.toyproject.t4lk.chat.ChatMessageRepository;
import com.toyproject.t4lk.chat.ChatMessageType;
import com.toyproject.t4lk.room.Room;
import com.toyproject.t4lk.room.RoomRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner initializeSampleData(
            RoomRepository roomRepository,
            ChatMessageRepository chatMessageRepository,
            @Value("${app.seed.enabled:true}") boolean seedEnabled
    ) {
        return args -> {
            if (!seedEnabled || roomRepository.count() > 0) {
                return;
            }

            Room freeTalk = roomRepository.save(new Room("자유 대화실", "누구나 편하게 이야기하는 기본 방", true));
            Room midnightTalk = roomRepository.save(new Room("심야 잡담방", "밤 시간대 가볍게 이야기하는 방", true));
            Room retroTalk = roomRepository.save(new Room("추억의 PC통신방", "레트로 감성으로 대화하는 컨셉 방", false));

            chatMessageRepository.saveAll(List.of(
                    new ChatMessage(freeTalk, "명예로운 팬티_192.168", ChatMessageType.CHAT, "안녕하세요. 반갑습니다."),
                    new ChatMessage(freeTalk, "용감한 고양이_192.168", ChatMessageType.CHAT, "여기 분위기 좋네요."),
                    new ChatMessage(midnightTalk, "조용한 모뎀_192.168", ChatMessageType.CHAT, "심야 잡담방 테스트 중입니다."),
                    new ChatMessage(retroTalk, "날카로운 사용자_192.168", ChatMessageType.SYSTEM, "현재 비활성화된 방입니다.")
            ));
        };
    }
}
