package com.toyproject.t4lk.member;

public class DuplicateDisplayNameException extends RuntimeException {

    public DuplicateDisplayNameException(String displayName) {
        super("이미 사용 중인 닉네임입니다. displayName=" + displayName);
    }
}
