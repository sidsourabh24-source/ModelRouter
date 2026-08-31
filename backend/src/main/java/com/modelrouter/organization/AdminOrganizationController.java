package com.modelrouter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orgs")
@RequiredArgsConstructor
public class AdminOrganizationController {

    private final OrganizationRepository organizationRepository;

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization org) {
        if (org.getId() == null || org.getId().isBlank()) {
            org.setId("org-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (org.getPlan() == null || org.getPlan().isBlank()) {
            org.setPlan("FREE");
        }
        Organization saved = organizationRepository.save(org);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Organization>> listOrganizations() {
        return ResponseEntity.ok(organizationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganization(@PathVariable String id) {
        return organizationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
