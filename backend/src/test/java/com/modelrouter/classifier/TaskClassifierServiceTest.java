package com.modelrouter.classifier;

import com.modelrouter.routing.InferenceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskClassifierServiceTest {

    private TaskClassifierService taskClassifierService;

    @BeforeEach
    void setUp() {
        taskClassifierService = new TaskClassifierService();
    }

    @Test
    void testCodeClassification() {
        InferenceRequest request = InferenceRequest.builder()
                .messages(List.of(
                        InferenceRequest.ChatMessage.builder()
                                .role("user")
                                .content("Please write a Java function to sort a list using quicksort:\n```java\npublic void sort() {}\n```")
                                .build()
                ))
                .build();

        TaskClassificationResult result = taskClassifierService.classify(request);
        assertNotNull(result);
        assertEquals(TaskClassificationResult.TaskCategory.CODE, result.getCategory());
        assertEquals("code", result.getRecommendedCapability());
        assertTrue(result.getComplexityScore() > 0.3);
    }

    @Test
    void testReasoningClassification() {
        InferenceRequest request = InferenceRequest.builder()
                .messages(List.of(
                        InferenceRequest.ChatMessage.builder()
                                .role("user")
                                .content("Calculate and evaluate step-by-step why the limit as x approaches infinity of (1 + 1/x)^x equals e.")
                                .build()
                ))
                .build();

        TaskClassificationResult result = taskClassifierService.classify(request);
        assertNotNull(result);
        assertEquals(TaskClassificationResult.TaskCategory.REASONING, result.getCategory());
        assertEquals("reasoning", result.getRecommendedCapability());
    }

    @Test
    void testWritingClassification() {
        InferenceRequest request = InferenceRequest.builder()
                .messages(List.of(
                        InferenceRequest.ChatMessage.builder()
                                .role("user")
                                .content("Write a creative essay and blog post about the beauty of sunset over mountains.")
                                .build()
                ))
                .build();

        TaskClassificationResult result = taskClassifierService.classify(request);
        assertNotNull(result);
        assertEquals(TaskClassificationResult.TaskCategory.WRITING, result.getCategory());
        assertEquals("writing", result.getRecommendedCapability());
    }

    @Test
    void testChatClassificationFallback() {
        InferenceRequest request = InferenceRequest.builder()
                .messages(List.of(
                        InferenceRequest.ChatMessage.builder()
                                .role("user")
                                .content("Hello, how are you doing today?")
                                .build()
                ))
                .build();

        TaskClassificationResult result = taskClassifierService.classify(request);
        assertNotNull(result);
        assertEquals(TaskClassificationResult.TaskCategory.CHAT, result.getCategory());
        assertEquals("chat", result.getRecommendedCapability());
        assertTrue(result.getComplexityScore() <= 0.3);
    }
}
