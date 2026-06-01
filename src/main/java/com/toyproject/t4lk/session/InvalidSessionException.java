package com.toyproject.t4lk.session;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("유효하지 않은 세션 토큰입니다.");
    }
}
