package com.sp.poc.rsm.entity;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<String> {
    // In realtime we grab user info from authentication or context
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Sreeni Padarthi");
    }
}
