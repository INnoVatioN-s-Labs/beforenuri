package com.toyproject.t4lk.chat;

import java.util.List;

import com.toyproject.t4lk.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
            description = "선택한 채팅방의 최근 메시지 목록을 조회합니다. 현재는 Swagger 테스트용 임시 데이터가 반환됩니다."
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
}
