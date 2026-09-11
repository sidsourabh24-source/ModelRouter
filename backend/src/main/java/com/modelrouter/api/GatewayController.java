package com.modelrouter.api;

import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import com.modelrouter.routing.RoutingEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GatewayController {

    private final RoutingEngineService routingEngineService;

    @PostMapping("/chat")
    public ResponseEntity<?> processChatInference(@RequestBody(required = false) InferenceRequest request) {
        try {
            if (request == null) {
                request = new InferenceRequest();
            }

            // 1. Resolve Organization ID from Security Context if authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String orgId && !orgId.isBlank()) {
                if (request.getOrganizationId() == null || request.getOrganizationId().isBlank()) {
                    request.setOrganizationId(orgId);
                }
            }

            // 2. Default Message Fallback
            if (request.getMessages() == null || request.getMessages().isEmpty()) {
                InferenceRequest.ChatMessage defaultMsg = InferenceRequest.ChatMessage.builder()
                        .role("user")
                        .content("Hello ModelRouter")
                        .build();
                request.setMessages(java.util.List.of(defaultMsg));
            }

            // 3. Execute Routing Engine
            InferenceResponse response = routingEngineService.routeAndExecute(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Inference execution failed: ", e);
            return ResponseEntity.status(500).body(java.util.Map.of(
                    "error", "INFERENCE_EXECUTION_ERROR",
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error occurred during inference"
            ));
        }
    }
}

