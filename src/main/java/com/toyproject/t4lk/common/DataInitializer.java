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

            Room feelingTalk = roomRepository.save(new Room(1, "느낌있는 대화", "가볍게 감성적인 대화를 나누는 방", "평범함이 좋아", true));
            Room freeTalk = roomRepository.save(new Room(2, "자유로운 대화", "누구나 편하게 이야기하는 기본 방", "평범함이 좋아", true));
            roomRepository.save(new Room(3, "초보자 대화실", "처음 온 이용자들이 적응하는 방", "평범함이 좋아", true));
            roomRepository.save(new Room(4, "타자 방", "빠르게 타자를 치며 노는 방", "평범함이 좋아", true));
            roomRepository.save(new Room(21, "서울특별시", "서울 지역 이용자들이 모이는 대화방", "지역별 대화실", true));
            roomRepository.save(new Room(22, "인천/경기/강원", "수도권과 강원 지역 이용자 방", "지역별 대화실", true));
            roomRepository.save(new Room(23, "대전/충청", "충청권 이용자 대화방", "지역별 대화실", true));
            roomRepository.save(new Room(24, "광주/전라", "전라권 이용자 대화방", "지역별 대화실", true));
            roomRepository.save(new Room(25, "대구/울산/경상", "경상권 이용자 대화방", "지역별 대화실", true));
            roomRepository.save(new Room(26, "부산/제주", "부산과 제주 지역 이용자 대화방", "지역별 대화실", true));
            roomRepository.save(new Room(41, "초등학생 끼리끼리", "초등학생 이용자 대화방", "우리끼리 좋아", true));
            roomRepository.save(new Room(42, "중학생 모여라", "중학생 이용자 대화방", "우리끼리 좋아", true));
            roomRepository.save(new Room(43, "고등학생 대화실", "고등학생 이용자 대화방", "우리끼리 좋아", true));
            roomRepository.save(new Room(44, "대학생 대화실", "대학생 이용자 대화방", "우리끼리 좋아", true));
            roomRepository.save(new Room(45, "직장인의 휴식처", "직장인 이용자 대화방", "우리끼리 좋아", true));
            Room gameTalk = roomRepository.save(new Room(46, "게임좋아하는 사람", "게임 이야기를 나누는 방", "우리끼리 좋아", true));

            chatMessageRepository.saveAll(List.of(
                    new ChatMessage(feelingTalk, "명예로운 팬티_192.168", ChatMessageType.CHAT, "안녕하세요. 반갑습니다."),
                    new ChatMessage(freeTalk, "용감한 고양이_192.168", ChatMessageType.CHAT, "여기 분위기 좋네요."),
                    new ChatMessage(gameTalk, "조용한 모뎀_192.168", ChatMessageType.CHAT, "게임 이야기 환영합니다."),
                    new ChatMessage(gameTalk, "날카로운 사용자_192.168", ChatMessageType.SYSTEM, "매너 있는 대화를 부탁드립니다.")
            ));
        };
    }
}
