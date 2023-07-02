package com.adminseeker.authservice.logging;

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

import com.adminseeker.authservice.dto.AuthRequest;
import com.adminseeker.authservice.proxies.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class LoggerComponent {
    
    private static final Logger log = LoggerFactory.getLogger(LoggerComponent.class);
    
    @Pointcut("within(com.adminseeker.authservice.controllers..*)")
    private void controller() {}

    @Pointcut("within(com.adminseeker.authservice..*)")
    private void exceptions() {}

    @Autowired
    HttpServletRequest request;

    

    @Around("controller() && args(@org.springframework.web.bind.annotation.RequestHeader headers,@org.springframework.web.bind.annotation.RequestBody body,..)")
    public Object logForRequestWithBody(ProceedingJoinPoint pjp, Object headers,Object body) throws Exception,Throwable{
        ObjectMapper mapper = new ObjectMapper();

        log.info("Request Headers : {}" ,mapper.writeValueAsString(headers));
        log.info("Request: method={}, uri={}",request.getMethod(),request.getRequestURI());
        
        String reqBodyString = mapper.writeValueAsString(body);
        final long startTime = System.currentTimeMillis();

        Object object = pjp.proceed();

        String respBodyString = mapper.writeValueAsString(object);

        if(request.getRequestURI().equals("/api/v1/auth/register")){
            User user = mapper.readValue(reqBodyString,User.class);
            user.setPassword("***********");
            reqBodyString = mapper.writeValueAsString(user);

        }
        if(request.getRequestURI().contains("/api/v1/auth/login") ){
            AuthRequest authRequest = mapper.readValue(reqBodyString,AuthRequest.class);
            authRequest.setPassword("***********");
            reqBodyString = mapper.writeValueAsString(authRequest);
        }

        log.info("Request Body : {}",reqBodyString);
    

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