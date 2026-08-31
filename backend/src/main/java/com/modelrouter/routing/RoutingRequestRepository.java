package com.modelrouter.routing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoutingRequestRepository extends JpaRepository<RoutingRequest, String> {
    List<RoutingRequest> findByOrganizationId(String organizationId);
    List<RoutingRequest> findBySelectedModelId(String selectedModelId);
}
