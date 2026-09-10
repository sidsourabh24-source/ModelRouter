package com.modelrouter;

import com.modelrouter.classifier.TaskClassificationResult;
import com.modelrouter.classifier.TaskClassifierService;
import com.modelrouter.provider.Model;
import com.modelrouter.routing.CandidateFilterEngine;
import com.modelrouter.routing.RoutingEngineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarkRunnerService {

    @Test
    void testRoutingOverheadIsUnder15ms() {
        TaskClassifierService classifier = new TaskClassifierService();
        CandidateFilterEngine filterEngine = new CandidateFilterEngine(null);

        Model model1 = Model.builder().id("m1").name("gpt-4o").inputPricePer1k(BigDecimal.valueOf(0.0025)).status("ACTIVE").build();
        Model model2 = Model.builder().id("m2").name("mock-cheap").inputPricePer1k(BigDecimal.valueOf(0.0001)).status("ACTIVE").build();

        RoutingEngineService routingEngine = new RoutingEngineService(
                Mockito.mock(com.modelrouter.provider.ModelRepository.class),
                Mockito.mock(com.modelrouter.routing.RoutingRequestRepository.class),
                classifier,
                filterEngine,
                Mockito.mock(com.modelrouter.routing.FallbackExecutionEngine.class),
                null
        );

        long start = System.nanoTime();
        TaskClassificationResult classification = classifier.classify(null);
        Model selected = routingEngine.selectBestModel(List.of(model1, model2), "CHEAP", classification);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        assertNotNull(selected);
        assertTrue(elapsedMs < 15, "Routing overhead should be under 15ms");
    }
}
