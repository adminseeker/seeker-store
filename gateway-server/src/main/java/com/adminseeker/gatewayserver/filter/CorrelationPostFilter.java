package com.adminseeker.gatewayserver.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

@Configuration
public class CorrelationPostFilter {

	@Autowired
	FilterUtility filterUtility;
	
	@Bean
	public GlobalFilter postGlobalFilter() {
		return (exchange, chain) -> {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
				String correlationId = filterUtility.getCorrelationId(requestHeaders);
				exchange.getResponse().getHeaders().add(FilterUtility.CORRELATION_ID, correlationId);
			}));
		};
	}
}