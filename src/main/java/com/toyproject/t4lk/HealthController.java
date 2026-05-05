package com.toyproject.t4lk;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "System", description = "서버 기본 상태를 확인하는 시스템 API")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "서버 상태 확인",
            description = "애플리케이션이 실행 중인지, HTTP 요청과 JSON 응답이 정상 동작하는지 확인합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "서버가 정상 동작 중입니다.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "status": "ok"
                            }
                            """)
            )
    )
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
