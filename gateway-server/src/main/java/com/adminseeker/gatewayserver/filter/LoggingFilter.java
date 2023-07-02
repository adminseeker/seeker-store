package com.adminseeker.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.adminseeker.gatewayserver.utils.RequestLoggingInterceptor;
import com.adminseeker.gatewayserver.utils.ResponseLoggingInterceptor;

import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Component
public class LoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private String ignorePatterns=null;

   

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (ignorePatterns != null && exchange.getRequest().getURI().getPath().matches(ignorePatterns)) {
            return chain.filter(exchange);
        }else {
            final long startTime = System.currentTimeMillis();
            XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
            InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
            LOGGER.info("Request Origin IP: {}", inetSocketAddress.getAddress().getHostAddress());
            LOGGER.info("Request Headers: {}",exchange.getRequest().getHeaders().toString());
            LOGGER.info("Request: method={}, uri={}", exchange.getRequest().getMethod(),exchange.getRequest().getPath());
            ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {
                return new RequestLoggingInterceptor(super.getRequest());
            }

            @Override
            public ServerHttpResponse getResponse() {
                return new ResponseLoggingInterceptor(super.getResponse(), startTime);
            }
        };
            return chain.filter(exchangeDecorator);
        }
    }
}