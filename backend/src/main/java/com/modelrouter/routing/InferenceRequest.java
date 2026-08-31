package com.modelrouter.routing;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InferenceRequest {

    @Singular
    private List<ChatMessage> messages;

    @Builder.Default
    private String mode = "BALANCED";

    private Integer maxTokens;
    private Double temperature;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
