package com.toyproject.t4lk.session;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousSessionRepository extends JpaRepository<AnonymousSession, Long> {
}
