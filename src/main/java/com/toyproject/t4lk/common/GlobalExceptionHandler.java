package com.toyproject.t4lk.common;

import com.toyproject.t4lk.room.RoomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoomNotFound(RoomNotFoundException exception) {
        return new ErrorResponse("ROOM_NOT_FOUND", exception.getMessage());
    }
}
