package com.modelrouter.api;

import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import com.modelrouter.routing.RoutingEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GatewayController {

    private final RoutingEngineService routingEngineService;

    @PostMapping("/chat")
    public ResponseEntity<InferenceResponse> processChatInference(@RequestBody InferenceRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            InferenceRequest.ChatMessage defaultMsg = InferenceRequest.ChatMessage.builder()
                    .role("user")
                    .content("Hello ModelRouter")
                    .build();
            request.setMessages(java.util.List.of(defaultMsg));
        }

        InferenceResponse response = routingEngineService.routeAndExecute(request);
        return ResponseEntity.ok(response);
    }
}
