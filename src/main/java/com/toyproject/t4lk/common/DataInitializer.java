package com.toyproject.t4lk.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private static final List<RoomSeed> ROOM_SEEDS = List.of(
            new RoomSeed(1, "느낌있는 대화", "가볍게 감성적인 대화를 나누는 방", "평범함이 좋아", true, List.of("심야 잡담방")),
            new RoomSeed(2, "자유로운 대화", "누구나 편하게 이야기하는 기본 방", "평범함이 좋아", true, List.of("자유 대화실")),
            new RoomSeed(3, "초보자 대화실", "처음 온 이용자들이 적응하는 방", "평범함이 좋아", true, List.of()),
            new RoomSeed(4, "타자 방", "빠르게 타자를 치며 노는 방", "평범함이 좋아", true, List.of()),
            new RoomSeed(21, "서울특별시", "서울 지역 이용자들이 모이는 대화방", "지역별 대화실", true, List.of()),
            new RoomSeed(22, "인천/경기/강원", "수도권과 강원 지역 이용자 방", "지역별 대화실", true, List.of()),
            new RoomSeed(23, "대전/충청", "충청권 이용자 대화방", "지역별 대화실", true, List.of()),
            new RoomSeed(24, "광주/전라", "전라권 이용자 대화방", "지역별 대화실", true, List.of()),
            new RoomSeed(25, "대구/울산/경상", "경상권 이용자 대화방", "지역별 대화실", true, List.of()),
            new RoomSeed(26, "부산/제주", "부산과 제주 지역 이용자 대화방", "지역별 대화실", true, List.of()),
            new RoomSeed(41, "초등학생 끼리끼리", "초등학생 이용자 대화방", "우리끼리 좋아", true, List.of()),
            new RoomSeed(42, "중학생 모여라", "중학생 이용자 대화방", "우리끼리 좋아", true, List.of()),
            new RoomSeed(43, "고등학생 대화실", "고등학생 이용자 대화방", "우리끼리 좋아", true, List.of()),
            new RoomSeed(44, "대학생 대화실", "대학생 이용자 대화방", "우리끼리 좋아", true, List.of()),
            new RoomSeed(45, "직장인의 휴식처", "직장인 이용자 대화방", "우리끼리 좋아", true, List.of()),
            new RoomSeed(46, "게임좋아하는 사람", "게임 이야기를 나누는 방", "우리끼리 좋아", true, List.of())
    );

    @Bean
    ApplicationRunner initializeSampleData(
            RoomRepository roomRepository,
            ChatMessageRepository chatMessageRepository,
            @Value("${app.seed.enabled:true}") boolean seedEnabled
    ) {
        return args -> {
            if (!seedEnabled) {
                return;
            }

            Map<Integer, Room> seededRooms = upsertRooms(roomRepository);
            seedMessagesIfEmpty(chatMessageRepository, seededRooms);
        };
    }

    private Map<Integer, Room> upsertRooms(RoomRepository roomRepository) {
        List<Room> existingRooms = roomRepository.findAllByIsDeletedFalseOrderByCodeAsc();
        Map<String, Room> roomByTitle = new HashMap<>();
        for (Room room : existingRooms) {
            roomByTitle.put(room.getTitle(), room);
        }

        Map<Integer, Room> seededRooms = new HashMap<>();
        for (RoomSeed seed : ROOM_SEEDS) {
            Room room = roomRepository.findByCodeAndIsDeletedFalse(seed.code())
                    .or(() -> findLegacyRoom(roomByTitle, seed.legacyTitles()))
                    .orElseGet(() -> new Room(seed.code(), seed.title(), seed.description(), seed.category(), seed.active()));

            room.update(seed.code(), seed.title(), seed.description(), seed.category(), seed.active());
            seededRooms.put(seed.code(), roomRepository.save(room));
        }
        return seededRooms;
    }

    private Optional<Room> findLegacyRoom(Map<String, Room> roomByTitle, List<String> legacyTitles) {
        return legacyTitles.stream()
                .map(roomByTitle::get)
                .filter(room -> room != null)
                .findFirst();
    }

    private void seedMessagesIfEmpty(ChatMessageRepository chatMessageRepository, Map<Integer, Room> seededRooms) {
        if (chatMessageRepository.count() > 0) {
            return;
        }

        Room feelingTalk = seededRooms.get(1);
        Room freeTalk = seededRooms.get(2);
        Room gameTalk = seededRooms.get(46);

        chatMessageRepository.saveAll(List.of(
                new ChatMessage(feelingTalk, "명예로운 팬티_192.168", ChatMessageType.CHAT, "안녕하세요. 반갑습니다."),
                new ChatMessage(freeTalk, "용감한 고양이_192.168", ChatMessageType.CHAT, "여기 분위기 좋네요."),
                new ChatMessage(gameTalk, "조용한 모뎀_192.168", ChatMessageType.CHAT, "게임 이야기 환영합니다."),
                new ChatMessage(gameTalk, "날카로운 사용자_192.168", ChatMessageType.SYSTEM, "매너 있는 대화를 부탁드립니다.")
        ));
    }

    private record RoomSeed(
            int code,
            String title,
            String description,
            String category,
            boolean active,
            List<String> legacyTitles
    ) {
    }
}
