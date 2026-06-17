package com.toyproject.t4lk.chat.socket;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms/{roomId}/occupants")
@Tag(name = "Presence", description = "대화실 실시간 접속자 조회 API")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @GetMapping
    @Operation(
            summary = "대화실 접속자 목록 조회",
            description = "현재 해당 대화실에 실시간 접속 중인 닉네임 목록을 반환합니다. (/목록·/who 명령용)"
    )
    public List<String> getOccupants(@PathVariable Long roomId) {
        return presenceService.occupants(roomId);
    }
}
