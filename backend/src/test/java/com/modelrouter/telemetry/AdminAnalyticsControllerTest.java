package com.modelrouter.telemetry;

import com.modelrouter.routing.RoutingRequest;
import com.modelrouter.routing.RoutingRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AdminAnalyticsControllerTest {

    private RoutingRequestRepository repository;
    private AdminAnalyticsController controller;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RoutingRequestRepository.class);
        controller = new AdminAnalyticsController(repository);
    }

    @Test
    void testGetOverviewCalculation() {
        RoutingRequest req1 = RoutingRequest.builder()
                .id("req-1")
                .latencyMs(100)
                .estimatedCost(BigDecimal.valueOf(0.002))
                .cacheHit(false)
                .build();

        RoutingRequest req2 = RoutingRequest.builder()
                .id("req-2")
                .latencyMs(10)
                .estimatedCost(BigDecimal.valueOf(0.000))
                .cacheHit(true)
                .build();

        when(repository.findAll()).thenReturn(List.of(req1, req2));

        ResponseEntity<AdminAnalyticsController.AnalyticsOverviewResponse> response = controller.getOverview();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        AdminAnalyticsController.AnalyticsOverviewResponse overview = response.getBody();
        assertNotNull(overview);
        assertEquals(2L, overview.getTotalRequests());
        assertEquals(BigDecimal.valueOf(50.0).setScale(2), overview.getAvgLatencyMs());
        assertEquals(BigDecimal.valueOf(50.0).setScale(2), overview.getCacheHitRatePercent());
    }
}
