package com.adminseeker.cartservice.services;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.adminseeker.cartservice.entities.ErrorResponse;
import com.adminseeker.cartservice.exceptions.ResourceUpdateError;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignerrorMessageHandler implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ErrorResponse.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return new ResourceUpdateError(message.getMsg());
    }
}
