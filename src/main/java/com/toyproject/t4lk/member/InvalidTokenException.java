package com.toyproject.t4lk.member;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("유효하지 않거나 만료된 토큰입니다.");
    }
}
