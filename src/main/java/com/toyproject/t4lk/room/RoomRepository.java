package com.toyproject.t4lk.room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByIsDeletedFalseOrderByIdAsc();

    Optional<Room> findByIdAndIsDeletedFalse(Long id);
}
