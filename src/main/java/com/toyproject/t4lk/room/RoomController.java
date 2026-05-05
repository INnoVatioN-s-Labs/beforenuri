package com.toyproject.t4lk.room;

import java.util.List;

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

import com.toyproject.t4lk.common.ErrorResponse;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "채팅방 조회와 관련된 API")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(
            summary = "채팅방 목록 조회",
            description = "입장 가능한 채팅방 목록을 조회합니다. 현재는 Swagger 테스트를 위한 임시 데이터가 반환됩니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 목록 조회에 성공했습니다.",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RoomResponse.class))
            )
    )
    public List<RoomResponse> getRooms() {
        return roomService.getRooms();
    }

    @GetMapping("/{roomId}")
    @Operation(
            summary = "채팅방 상세 조회",
            description = "선택한 채팅방 하나의 상세 정보를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 상세 조회에 성공했습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RoomResponse.class)
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
    public RoomResponse getRoom(@PathVariable Long roomId) {
        return roomService.getRoom(roomId);
    }
}
