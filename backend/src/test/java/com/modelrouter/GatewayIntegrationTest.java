package com.modelrouter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelrouter.routing.InferenceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GatewayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChatEndpointWithoutApiKeyReturnsUnauthorized() throws Exception {
        InferenceRequest request = InferenceRequest.builder()
                .mode("BALANCED")
                .messages(List.of(InferenceRequest.ChatMessage.builder().role("user").content("Hello").build()))
                .build();

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testChatEndpointWithValidKeyReturnsResponseAndRoutingTrace() throws Exception {
        InferenceRequest request = InferenceRequest.builder()
                .mode("BALANCED")
                .messages(List.of(InferenceRequest.ChatMessage.builder().role("user").content("Write quicksort in Java").build()))
                .build();

        mockMvc.perform(post("/api/v1/chat")
                        .header("X-API-Key", "modelrouter-demo-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routing").exists())
                .andExpect(jsonPath("$.routing.taskCategory").value("CODE"));
    }

    @Test
    void testAnalyticsOverviewEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/admin/analytics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").exists());
    }
}
