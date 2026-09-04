package com.modelrouter.routing;

import com.modelrouter.classifier.TaskClassificationResult;
import com.modelrouter.provider.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateFilterEngineTest {

    private CandidateFilterEngine filterEngine;
    private Model modelActiveCode;
    private Model modelActiveChat;
    private Model modelInactive;

    @BeforeEach
    void setUp() {
        filterEngine = new CandidateFilterEngine();

        modelActiveCode = Model.builder()
                .id("model-code")
                .name("claude-3-5-sonnet")
                .status("ACTIVE")
                .capabilities("chat,code,reasoning")
                .contextLimit(200000)
                .inputPricePer1k(BigDecimal.valueOf(0.003))
                .build();

        modelActiveChat = Model.builder()
                .id("model-chat")
                .name("gpt-3.5-turbo")
                .status("ACTIVE")
                .capabilities("chat")
                .contextLimit(4000)
                .inputPricePer1k(BigDecimal.valueOf(0.0005))
                .build();

        modelInactive = Model.builder()
                .id("model-off")
                .name("old-deprecated-model")
                .status("INACTIVE")
                .capabilities("chat")
                .contextLimit(2000)
                .build();
    }

    @Test
    void testFilterInactiveModels() {
        TaskClassificationResult classification = TaskClassificationResult.builder()
                .recommendedCapability("chat")
                .estimatedPromptTokens(500)
                .build();

        List<Model> filtered = filterEngine.filterCandidates(List.of(modelActiveCode, modelActiveChat, modelInactive), classification);
        assertEquals(2, filtered.size());
        assertFalse(filtered.contains(modelInactive));
    }

    @Test
    void testFilterByContextLimit() {
        TaskClassificationResult classification = TaskClassificationResult.builder()
                .recommendedCapability("chat")
                .estimatedPromptTokens(10000) // Exceeds gpt-3.5-turbo limit (4000)
                .build();

        List<Model> filtered = filterEngine.filterCandidates(List.of(modelActiveCode, modelActiveChat), classification);
        assertEquals(1, filtered.size());
        assertEquals("model-code", filtered.get(0).getId());
    }

    @Test
    void testFilterByCapability() {
        TaskClassificationResult classification = TaskClassificationResult.builder()
                .recommendedCapability("code")
                .estimatedPromptTokens(500)
                .build();

        List<Model> filtered = filterEngine.filterCandidates(List.of(modelActiveCode, modelActiveChat), classification);
        assertEquals(1, filtered.size());
        assertEquals("model-code", filtered.get(0).getId());
    }
}
