package com.toyproject.t4lk.room;

import com.toyproject.t4lk.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer code;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false)
    private boolean active;

    protected Room() {
    }

    public Room(Integer code, String title, String description, String category, boolean active) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.category = category;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Integer getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public void update(Integer code, String title, String description, String category, boolean active) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.category = category;
        this.active = active;
    }
}
