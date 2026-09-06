package com.modelrouter.telemetry;

import com.modelrouter.routing.RoutingRequest;
import com.modelrouter.routing.RoutingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTelemetryService {

    private final RoutingRequestRepository routingRequestRepository;

    @Async
    public void recordTelemetryAsync(RoutingRequest telemetry) {
        if (telemetry == null || routingRequestRepository == null) return;
        try {
            routingRequestRepository.save(telemetry);
            log.debug("Async telemetry persisted for request {}", telemetry.getRequestId());
        } catch (Exception e) {
            log.warn("Failed to persist async telemetry for request {}: {}", telemetry.getRequestId(), e.getMessage());
        }
    }
}
