package com.modelrouter.provider;

import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;

public interface ModelProvider {
    String getProviderId();
    String getProviderName();
    boolean isHealthy();
    InferenceResponse executeInference(Model model, InferenceRequest request);
}
