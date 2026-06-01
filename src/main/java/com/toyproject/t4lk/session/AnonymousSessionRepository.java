package com.toyproject.t4lk.session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousSessionRepository extends JpaRepository<AnonymousSession, Long> {

    Optional<AnonymousSession> findBySessionTokenAndIsDeletedFalse(String sessionToken);
}
