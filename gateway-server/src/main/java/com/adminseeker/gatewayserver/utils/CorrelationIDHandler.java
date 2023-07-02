package com.adminseeker.gatewayserver.utils;


import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CorrelationIDHandler {
    private final Tracer tracer;

    public CorrelationIDHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    public String getCorrelationId() {
        return Optional.of(tracer).map(Tracer::currentSpan).map(Span::context).map(TraceContext::traceId).orElse("");
    }
}