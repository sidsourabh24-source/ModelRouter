package com.modelrouter.common;

import com.modelrouter.auth.ApiKey;
import com.modelrouter.auth.ApiKeyAuthenticationFilter;
import com.modelrouter.auth.ApiKeyRepository;
import com.modelrouter.organization.Organization;
import com.modelrouter.organization.OrganizationRepository;
import com.modelrouter.provider.Model;
import com.modelrouter.provider.ModelRepository;
import com.modelrouter.provider.Provider;
import com.modelrouter.provider.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ProviderRepository providerRepository;
    private final ModelRepository modelRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing ModelRouter seed data in database...");

        // 1. Seed Organization
        if (!organizationRepository.existsById("org-demo-001")) {
            Organization org = Organization.builder()
                    .id("org-demo-001")
                    .name("Acme Corp AI Team")
                    .plan("ENTERPRISE")
                    .budgetLimit(BigDecimal.valueOf(1000.00))
                    .build();
            organizationRepository.save(org);
        }

        // 2. Seed Demo API Key (modelrouter-demo-key-123)
        String demoKeyHash = ApiKeyAuthenticationFilter.hashApiKey("modelrouter-demo-key-123");
        if (apiKeyRepository.findByKeyHashAndStatus(demoKeyHash, "ACTIVE").isEmpty()) {
            ApiKey apiKey = ApiKey.builder()
                    .id("key-demo-001")
                    .organizationId("org-demo-001")
                    .name("Demo API Key")
                    .keyHash(demoKeyHash)
                    .status("ACTIVE")
                    .build();
            apiKeyRepository.save(apiKey);
        }

        // 3. Seed Providers
        if (providerRepository.count() == 0) {
            Provider provOpenAi = Provider.builder().id("prov-openai").name("OpenAI").baseUrl("https://api.openai.com/v1").status("ACTIVE").build();
            Provider provAnthropic = Provider.builder().id("prov-anthropic").name("Anthropic").baseUrl("https://api.anthropic.com/v1").status("ACTIVE").build();
            Provider provMock = Provider.builder().id("prov-mock").name("Mock Provider").baseUrl("https://mock.modelrouter.internal").status("ACTIVE").build();
            providerRepository.saveAll(List.of(provOpenAi, provAnthropic, provMock));
        }

        // 4. Seed Models Catalog
        if (modelRepository.count() == 0) {
            Model gpt4o = Model.builder()
                    .id("model-gpt-4o")
                    .providerId("prov-openai")
                    .name("gpt-4o")
                    .capabilities("chat,code,vision,reasoning")
                    .contextLimit(128000)
                    .inputPricePer1k(BigDecimal.valueOf(0.0025))
                    .outputPricePer1k(BigDecimal.valueOf(0.0100))
                    .qualityScore(BigDecimal.valueOf(0.95))
                    .latencyScore(BigDecimal.valueOf(0.70))
                    .reliabilityScore(BigDecimal.valueOf(0.99))
                    .status("ACTIVE")
                    .build();

            Model sonnet = Model.builder()
                    .id("model-claude-3-5-sonnet")
                    .providerId("prov-anthropic")
                    .name("claude-3-5-sonnet")
                    .capabilities("chat,code,reasoning,writing")
                    .contextLimit(200000)
                    .inputPricePer1k(BigDecimal.valueOf(0.0030))
                    .outputPricePer1k(BigDecimal.valueOf(0.0150))
                    .qualityScore(BigDecimal.valueOf(0.96))
                    .latencyScore(BigDecimal.valueOf(0.65))
                    .reliabilityScore(BigDecimal.valueOf(0.98))
                    .status("ACTIVE")
                    .build();

            Model cheapMock = Model.builder()
                    .id("model-mock-cheap")
                    .providerId("prov-mock")
                    .name("mock-cheap-v1")
                    .capabilities("chat,code,reasoning,writing")
                    .contextLimit(32000)
                    .inputPricePer1k(BigDecimal.valueOf(0.00010))
                    .outputPricePer1k(BigDecimal.valueOf(0.00020))
                    .qualityScore(BigDecimal.valueOf(0.65))
                    .latencyScore(BigDecimal.valueOf(0.95))
                    .reliabilityScore(BigDecimal.valueOf(0.99))
                    .status("ACTIVE")
                    .build();

            modelRepository.saveAll(List.of(gpt4o, sonnet, cheapMock));
        }

        log.info("ModelRouter seed data initialization complete! Ready to accept requests.");
    }
}
