package com.adminseeker.gatewayserver.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;

import com.adminseeker.gatewayserver.dto.LogFilteredKeys;
import com.google.gson.Gson;


public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    public RequestLoggingInterceptor(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return super.getBody().doOnNext(dataBuffer -> {
            try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String body = IOUtils.toString(baos.toByteArray(), "UTF-8");
                LOGGER.info("Request Headers: {}",getDelegate().getHeaders().toString());
                

                LogFilteredKeys logFilteredKeys =new Gson().fromJson(body, LogFilteredKeys.class);
                if(StringUtils.isNotBlank(logFilteredKeys.getPassword())){
                    LOGGER.info("Request body: NOT LOGGED DUE TO SENSITIVE INFORMATION");
                }else{
                    LOGGER.info("Request body: {}",body);
                }
            } catch (IOException e) {
                LOGGER.error("ERROR: ", e.getMessage());
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    LOGGER.error("ERROR: ", e.getMessage());
                }
            }
        });
    }

}