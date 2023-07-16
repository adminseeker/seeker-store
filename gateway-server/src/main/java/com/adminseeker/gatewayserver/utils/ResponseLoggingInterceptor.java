package com.adminseeker.gatewayserver.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;

import com.adminseeker.gatewayserver.dto.LogFilteredKeys;
import com.adminseeker.gatewayserver.filter.LoggingFilter;
import com.google.gson.Gson;

import net.minidev.json.JSONObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;


public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

    private long startTime;

    public ResponseLoggingInterceptor(ServerHttpResponse delegate, long startTime) {
        super(delegate);
        this.startTime = startTime;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Flux<DataBuffer> buffer = Flux.from(body);
        return super.writeWith(buffer.doOnNext(dataBuffer -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String bodyRes = IOUtils.toString(baos.toByteArray(), "UTF-8");
                
                String responseLog=",Response = ";
                Long time = System.currentTimeMillis()-startTime;
                responseLog=responseLog.concat("Time: ").concat(Long.toString(time)).concat("ms");
                responseLog=responseLog.concat(",Headers: ").concat(JSONObject.escape(getDelegate().getHeaders().toString()));
                Integer status = getStatusCode().value();
                responseLog=responseLog.concat(",Status: ").concat(String.valueOf(status));
                responseLog=responseLog.concat(",Body: ").concat(JSONObject.escape(bodyRes));
                LOGGER.info(responseLog);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}