package com.toyproject.t4lk.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@Tag(name = "Session", description = "익명 사용자 세션 발급과 관련된 API")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/anonymous")
    @Operation(
            summary = "익명 세션 발급",
            description = "익명 채팅 입장을 위해 임시 세션 토큰과 표시용 닉네임을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "익명 세션 발급에 성공했습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AnonymousSessionResponse.class)
            )
    )
    public AnonymousSessionResponse issueAnonymousSession(HttpServletRequest request) {
        return sessionService.issueAnonymousSession(ClientIpResolver.resolveClientIp(request));
    }
}
