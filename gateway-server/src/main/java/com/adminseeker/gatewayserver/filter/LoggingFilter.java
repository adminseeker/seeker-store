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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

import net.minidev.json.JSONObject;

@Component
public class LoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private String ignorePatterns=null;

    private String requestLog = "";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        if (ignorePatterns != null && exchange.getRequest().getURI().getPath().matches(ignorePatterns)) {
            return chain.filter(exchange);
        }else {
            final long startTime = System.currentTimeMillis();
            XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
            InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
            try{
                ObjectMapper mapper = new ObjectMapper();
                requestLog = "Request = ";
                requestLog=requestLog.concat("Origin IP: ").concat(inetSocketAddress.getAddress().getHostAddress());
                requestLog=requestLog.concat(",Headers: ").concat(JSONObject.escape(mapper.writeValueAsString(exchange.getRequest().getHeaders().toString())));
                requestLog=requestLog.concat(",Method: ").concat(exchange.getRequest().getMethod().toString());
                requestLog=requestLog.concat(",URI: ").concat(exchange.getRequest().getPath().toString());
                if(exchange.getRequest().getMethod().toString().equals("GET") || exchange.getRequest().getMethod().toString().equals("DELETE")){
                    LOGGER.info(requestLog);
                }
            }catch(Exception e){
                LOGGER.info("Logging Error: {}", e);
            }
            
            ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
            
            @Override
            public ServerHttpRequest getRequest() {
                return new RequestLoggingInterceptor(super.getRequest(),requestLog);
            }


            @Override
            public ServerHttpResponse getResponse() {
                return new ResponseLoggingInterceptor(super.getResponse(), startTime);
            }
        };

            exchangeDecorator.mutate().request((request)->{
                
            }).build();
            return chain.filter(exchangeDecorator);
        }
    }
}