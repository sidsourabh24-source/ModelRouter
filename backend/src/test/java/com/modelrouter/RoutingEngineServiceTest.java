package com.modelrouter;

import com.modelrouter.classifier.TaskClassificationResult;
import com.modelrouter.classifier.TaskClassifierService;
import com.modelrouter.provider.Model;
import com.modelrouter.provider.ModelProvider;
import com.modelrouter.provider.ModelRepository;
import com.modelrouter.routing.CandidateFilterEngine;
import com.modelrouter.routing.FallbackExecutionEngine;
import com.modelrouter.routing.InferenceResponse;
import com.modelrouter.routing.RoutingEngineService;
import com.modelrouter.routing.RoutingRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RoutingEngineServiceTest {

    private ModelRepository modelRepository;
    private RoutingRequestRepository routingRequestRepository;
    private TaskClassifierService taskClassifierService;
    private CandidateFilterEngine candidateFilterEngine;
    private FallbackExecutionEngine fallbackExecutionEngine;
    private RoutingEngineService routingEngineService;

    @BeforeEach
    void setUp() {
        modelRepository = Mockito.mock(ModelRepository.class);
        routingRequestRepository = Mockito.mock(RoutingRequestRepository.class);
        taskClassifierService = Mockito.mock(TaskClassifierService.class);
        candidateFilterEngine = new CandidateFilterEngine();

        ModelProvider mockProvider = Mockito.mock(ModelProvider.class);
        when(mockProvider.getProviderId()).thenReturn("prov-mock");
        when(mockProvider.getProviderName()).thenReturn("Mock Provider");
        when(mockProvider.isHealthy()).thenReturn(true);
        when(mockProvider.executeInference(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> InferenceResponse.builder()
                        .requestId("req-test-123")
                        .model(((Model) invocation.getArgument(0)).getName())
                        .provider("Mock Provider")
                        .content("Test response")
                        .cacheHit(false)
                        .usage(InferenceResponse.UsageMetrics.builder()
                                .inputTokens(10)
                                .outputTokens(20)
                                .estimatedCost(BigDecimal.valueOf(0.0001))
                                .latencyMs(50L)
                                .build())
                        .build());

        fallbackExecutionEngine = new FallbackExecutionEngine(List.of(mockProvider));
        routingEngineService = new RoutingEngineService(
                modelRepository,
                routingRequestRepository,
                List.of(mockProvider),
                taskClassifierService,
                candidateFilterEngine,
                fallbackExecutionEngine
        );
    }

    @Test
    void testCheapModeSelectsLowestCostModel() {
        Model expensiveModel = Model.builder()
                .id("model-1")
                .providerId("prov-mock")
                .name("gpt-4o")
                .inputPricePer1k(BigDecimal.valueOf(0.005))
                .outputPricePer1k(BigDecimal.valueOf(0.015))
                .qualityScore(BigDecimal.valueOf(0.95))
                .latencyScore(BigDecimal.valueOf(0.70))
                .reliabilityScore(BigDecimal.valueOf(0.99))
                .build();

        Model cheapModel = Model.builder()
                .id("model-2")
                .providerId("prov-mock")
                .name("mock-cheap-v1")
                .inputPricePer1k(BigDecimal.valueOf(0.0001))
                .outputPricePer1k(BigDecimal.valueOf(0.0002))
                .qualityScore(BigDecimal.valueOf(0.65))
                .latencyScore(BigDecimal.valueOf(0.95))
                .reliabilityScore(BigDecimal.valueOf(0.99))
                .build();

        List<Model> candidates = List.of(expensiveModel, cheapModel);
        TaskClassificationResult classification = TaskClassificationResult.builder()
                .category(TaskClassificationResult.TaskCategory.CHAT)
                .recommendedCapability("chat")
                .build();

        Model selected = routingEngineService.selectBestModel(candidates, "CHEAP", classification);

        assertEquals("mock-cheap-v1", selected.getName());
    }

    @Test
    void testQualityModeSelectsHighestQualityModel() {
        Model expensiveModel = Model.builder()
                .id("model-1")
                .providerId("prov-mock")
                .name("gpt-4o")
                .inputPricePer1k(BigDecimal.valueOf(0.005))
                .outputPricePer1k(BigDecimal.valueOf(0.015))
                .qualityScore(BigDecimal.valueOf(0.98))
                .latencyScore(BigDecimal.valueOf(0.70))
                .reliabilityScore(BigDecimal.valueOf(0.99))
                .build();

        Model cheapModel = Model.builder()
                .id("model-2")
                .providerId("prov-mock")
                .name("mock-cheap-v1")
                .inputPricePer1k(BigDecimal.valueOf(0.0001))
                .outputPricePer1k(BigDecimal.valueOf(0.0002))
                .qualityScore(BigDecimal.valueOf(0.65))
                .latencyScore(BigDecimal.valueOf(0.95))
                .reliabilityScore(BigDecimal.valueOf(0.99))
                .build();

        List<Model> candidates = List.of(expensiveModel, cheapModel);
        TaskClassificationResult classification = TaskClassificationResult.builder()
                .category(TaskClassificationResult.TaskCategory.REASONING)
                .recommendedCapability("reasoning")
                .build();

        Model selected = routingEngineService.selectBestModel(candidates, "QUALITY", classification);

        assertEquals("gpt-4o", selected.getName());
    }
}
