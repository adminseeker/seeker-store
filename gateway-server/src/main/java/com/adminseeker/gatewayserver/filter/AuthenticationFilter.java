package com.adminseeker.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.adminseeker.gatewayserver.exceptions.LoginError;
import com.adminseeker.gatewayserver.proxies.TokenRequest;
import com.adminseeker.gatewayserver.proxies.UserTokenEmail;

import reactor.core.publisher.Mono;



@Component
@Order(2)
public class AuthenticationFilter implements GlobalFilter  {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
	FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (validator.isSecured.test(exchange.getRequest())){
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                logger.debug("missing authorization header");
                throw new LoginError("missing authorization header");
            }
            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setToken(authHeader);
            return webClientBuilder
                .baseUrl("http://authservice")
                .build()
                .post()
                .uri("/api/v1/auth/user")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("correlation-id", filterUtility.getCorrelationId(exchange.getRequest().getHeaders()))
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(tokenRequest),TokenRequest.class)
                .retrieve()
                .bodyToMono(UserTokenEmail.class)
                .map(userTokenEmail -> {
                    logger.debug("User Email: "+userTokenEmail.getEmail());
                    exchange.getRequest()
                            .mutate()
                            .header("X-auth-user-email", String.valueOf(userTokenEmail.getEmail()));
                    return exchange;
                })
                .onErrorResume(e -> {
                    logger.debug("Invalid Token");
                    return Mono.error(new LoginError("Invalid Token"));
                })
                .flatMap(chain::filter);
        }
        return chain.filter(exchange);
    };
}
