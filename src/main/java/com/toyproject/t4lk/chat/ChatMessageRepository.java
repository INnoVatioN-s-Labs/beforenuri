package com.toyproject.t4lk.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findTop4ByRoomIdAndDeletedFalseOrderByCreatedAtDesc(Long roomId);

    Optional<ChatMessage> findByIdAndRoomIdAndDeletedFalse(String id, Long roomId);
}
