package com.toyproject.t4lk.chat;

import java.util.List;

import com.toyproject.t4lk.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms/{roomId}/messages")
@Tag(name = "Messages", description = "채팅 메시지 조회와 관련된 API")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    @Operation(
            summary = "채팅방 메시지 목록 조회",
            description = "입장 직전 흐름 파악을 위해 가장 최근 4개의 메시지만 조회합니다. 이후 대화는 WebSocket 실시간 스트림으로 수신합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "메시지 목록 조회에 성공했습니다.",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class))
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 채팅방입니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public List<ChatMessageResponse> getMessages(@PathVariable Long roomId) {
        return chatService.getMessages(roomId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "채팅 메시지 생성",
            description = "선택한 채팅방에 새 메시지를 저장합니다."
    )
    public ChatMessageResponse createMessage(
            @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageUpsertRequest request
    ) {
        return chatService.createMessage(roomId, request);
    }

    @PutMapping("/{messageId}")
    @Operation(
            summary = "채팅 메시지 수정",
            description = "선택한 채팅방의 메시지를 수정합니다."
    )
    public ChatMessageResponse updateMessage(
            @PathVariable Long roomId,
            @PathVariable String messageId,
            @Valid @RequestBody ChatMessageUpsertRequest request
    ) {
        return chatService.updateMessage(roomId, messageId, request);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "채팅 메시지 삭제",
            description = "선택한 채팅방의 메시지를 소프트 삭제합니다."
    )
    public void deleteMessage(@PathVariable Long roomId, @PathVariable String messageId) {
        chatService.deleteMessage(roomId, messageId);
    }
}
