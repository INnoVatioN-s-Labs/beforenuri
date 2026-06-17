package com.toyproject.t4lk.common;

import com.toyproject.t4lk.chat.ChatMessageNotFoundException;
import com.toyproject.t4lk.member.DuplicateDisplayNameException;
import com.toyproject.t4lk.member.DuplicateUsernameException;
import com.toyproject.t4lk.member.InvalidCredentialsException;
import com.toyproject.t4lk.member.InvalidTokenException;
import com.toyproject.t4lk.post.PostNotFoundException;
import com.toyproject.t4lk.room.RoomCodeAlreadyExistsException;
import com.toyproject.t4lk.room.RoomNotFoundException;
import com.toyproject.t4lk.session.InvalidSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(ChatMessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleChatMessageNotFound(ChatMessageNotFoundException exception) {
        return new ErrorResponse("MESSAGE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFound(PostNotFoundException exception) {
        return new ErrorResponse("POST_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(RoomCodeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRoomCodeConflict(RoomCodeAlreadyExistsException exception) {
        return new ErrorResponse("ROOM_CODE_CONFLICT", exception.getMessage());
    }

    @ExceptionHandler(InvalidSessionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidSession(InvalidSessionException exception) {
        return new ErrorResponse("INVALID_SESSION", exception.getMessage());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateUsername(DuplicateUsernameException exception) {
        return new ErrorResponse("USERNAME_CONFLICT", exception.getMessage());
    }

    @ExceptionHandler(DuplicateDisplayNameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateDisplayName(DuplicateDisplayNameException exception) {
        return new ErrorResponse("DISPLAYNAME_CONFLICT", exception.getMessage());
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthFailure(RuntimeException exception) {
        return new ErrorResponse("UNAUTHORIZED", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("요청값이 올바르지 않습니다.");
        return new ErrorResponse("VALIDATION_ERROR", message);
    }
}
