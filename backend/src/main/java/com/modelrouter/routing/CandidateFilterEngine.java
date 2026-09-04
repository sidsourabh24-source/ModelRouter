package com.modelrouter.routing;

import com.modelrouter.classifier.TaskClassificationResult;
import com.modelrouter.provider.Model;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateFilterEngine {

    public List<Model> filterCandidates(List<Model> allModels, TaskClassificationResult classification) {
        if (allModels == null || allModels.isEmpty()) {
            return new ArrayList<>();
        }

        int promptTokens = classification != null ? classification.getEstimatedPromptTokens() : 0;
        String requiredCapability = classification != null ? classification.getRecommendedCapability() : "chat";

        // 1. Filter ACTIVE models
        List<Model> activeModels = allModels.stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()))
                .collect(Collectors.toList());

        if (activeModels.isEmpty()) {
            return allModels; // Fallback to avoid empty list if DB seed has non-ACTIVE items
        }

        // 2. Filter by Context Limit
        List<Model> contextFiltered = activeModels.stream()
                .filter(m -> m.getContextLimit() == null || m.getContextLimit() >= promptTokens)
                .collect(Collectors.toList());

        List<Model> pool = contextFiltered.isEmpty() ? activeModels : contextFiltered;

        // 3. Filter by Required Capability (if model explicitly defines capabilities)
        if (requiredCapability != null && !"chat".equalsIgnoreCase(requiredCapability)) {
            List<Model> capabilityFiltered = pool.stream()
                    .filter(m -> m.getCapabilities() != null && m.getCapabilities().toLowerCase().contains(requiredCapability.toLowerCase()))
                    .collect(Collectors.toList());

            if (!capabilityFiltered.isEmpty()) {
                return capabilityFiltered;
            }
        }

        return pool;
    }
}
