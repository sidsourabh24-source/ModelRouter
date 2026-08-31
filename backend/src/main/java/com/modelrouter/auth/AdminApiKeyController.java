package com.modelrouter.auth;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/keys")
@RequiredArgsConstructor
public class AdminApiKeyController {

    private final ApiKeyRepository apiKeyRepository;

    @Data
    public static class CreateKeyRequest {
        private String organizationId;
        private String name;
    }

    @Data
    @Builder
    public static class CreateKeyResponse {
        private String id;
        private String rawApiKey;
        private String organizationId;
        private String name;
        private String status;
    }

    @PostMapping
    public ResponseEntity<CreateKeyResponse> createApiKey(@RequestBody CreateKeyRequest request) {
        String keyId = "key-" + UUID.randomUUID().toString().substring(0, 8);
        String rawKey = "mr_live_" + UUID.randomUUID().toString().replace("-", "");
        String keyHash = ApiKeyAuthenticationFilter.hashApiKey(rawKey);

        ApiKey key = ApiKey.builder()
                .id(keyId)
                .organizationId(request.getOrganizationId())
                .name(request.getName())
                .keyHash(keyHash)
                .status("ACTIVE")
                .build();

        apiKeyRepository.save(key);

        CreateKeyResponse response = CreateKeyResponse.builder()
                .id(keyId)
                .rawApiKey(rawKey)
                .organizationId(request.getOrganizationId())
                .name(request.getName())
                .status("ACTIVE")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApiKey>> listKeys(@RequestParam String organizationId) {
        return ResponseEntity.ok(apiKeyRepository.findByOrganizationId(organizationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeKey(@PathVariable String id) {
        return apiKeyRepository.findById(id).map(key -> {
            key.setStatus("REVOKED");
            apiKeyRepository.save(key);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
