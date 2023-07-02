package com.adminseeker.productservice.logging;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Aspect
@Component
public class LoggerComponent {
    
    private static final Logger log = LoggerFactory.getLogger(LoggerComponent.class);
    
    @Pointcut("within(com.adminseeker.productservice.controllers..*) && args(@org.springframework.web.bind.annotation.RequestHeader headers,@org.springframework.web.bind.annotation.RequestBody body,..)")
    private void controllerWithBody(Object headers, Object body) {}

    @Pointcut("within(com.adminseeker.productservice.controllers..*) && args(@org.springframework.web.bind.annotation.RequestHeader headers)")
    private void controllerWithOnlyHeaders(Object headers) {}

    @Pointcut("within(com.adminseeker.productservice..*)")
    private void exceptions() {}

    @Autowired
    HttpServletRequest request;

    @Around("controllerWithBody(headers,body)")
    public Object logForRequests(ProceedingJoinPoint pjp, Object headers, Object body) throws Exception,Throwable{
        
        ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
       
        String reqBodyString = mapper.writeValueAsString(body);
        final long startTime = System.currentTimeMillis();

        Object object = pjp.proceed();

        String respBodyString = mapper.writeValueAsString(object);

        log.info("Request Headers : {}" ,mapper.writeValueAsString(headers));
        log.info("Request: method={}, uri={}",request.getMethod(),request.getRequestURI());
        if(!request.getMethod().equals("GET") && !request.getMethod().equals("DELETE")){
            log.info("Request Body : {}",reqBodyString);
        }
        log.info("Response({} ms) : {}",System.currentTimeMillis()-startTime,respBodyString);
        return object;
    }

    @Around("controllerWithOnlyHeaders(headers)")
    public Object logForRequestsWithOnlyHeaders(ProceedingJoinPoint pjp, Object headers) throws Exception,Throwable{
        
        ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Object object = pjp.proceed();
        final long startTime = System.currentTimeMillis();

        String respBodyString = mapper.writeValueAsString(object);

        log.info("Request Headers : {}" ,mapper.writeValueAsString(headers));
        log.info("Request: method={}, uri={}",request.getMethod(),request.getRequestURI());
        log.info("Response({} ms) : {}",System.currentTimeMillis()-startTime,respBodyString);
        return object;
    }

   

    @AfterThrowing(pointcut = "exceptions()", throwing = "e")
    public void logAfterException(JoinPoint jp, Exception e) {
        log.error("Exception during: {} with ex: {}", constructErrorLogMsg(jp),  e.toString());
    }

    private String constructErrorLogMsg(JoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        var sb = new StringBuilder("@");
        sb.append(method.getName());
        return sb.toString();
    }
}