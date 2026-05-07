package com.toyproject.t4lk.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByRoom_IdAndIsDeletedFalseOrderByCreatedAtAsc(Long roomId);

    Optional<ChatMessage> findByIdAndRoom_IdAndIsDeletedFalse(Long id, Long roomId);
}
