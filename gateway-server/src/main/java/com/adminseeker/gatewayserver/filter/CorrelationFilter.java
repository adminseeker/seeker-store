package com.adminseeker.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.adminseeker.gatewayserver.utils.CorrelationIDHandler;

import reactor.core.publisher.Mono;



@Component
@Order(1)
public class CorrelationFilter implements GlobalFilter  {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);
	
	@Autowired
	FilterUtility filterUtility;

	@Autowired
	CorrelationIDHandler correlationIDHandler;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
		if (isCorrelationIdPresent(requestHeaders)) {
		} else {
			String correlationID = generateCorrelationId();
			exchange = filterUtility.setCorrelationId(exchange, correlationID);
		}
		return chain.filter(exchange);
	}

	private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		if (filterUtility.getCorrelationId(requestHeaders) != null) {
			return true;
		} else {
			return false;
		}
	}

	private String generateCorrelationId() {
		return correlationIDHandler.getCorrelationId();
	}
}
