package com.toyproject.t4lk.session;

import com.toyproject.t4lk.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "anonymous_sessions")
public class AnonymousSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String sessionToken;

    @Column(nullable = false, length = 120)
    private String displayName;

    protected AnonymousSession() {
    }

    public AnonymousSession(String sessionToken, String displayName) {
        this.sessionToken = sessionToken;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getDisplayName() {
        return displayName;
    }
}
