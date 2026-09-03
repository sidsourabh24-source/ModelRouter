package com.modelrouter.classifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskClassificationResult {
    private TaskCategory category;
    private double complexityScore; // 0.0 (simple) to 1.0 (complex)
    private int estimatedPromptTokens;
    private String recommendedCapability;
    private List<String> detectedKeywords;

    public enum TaskCategory {
        CODE,
        REASONING,
        WRITING,
        CHAT
    }
}
