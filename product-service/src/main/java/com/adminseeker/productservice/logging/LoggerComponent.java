package com.adminseeker.productservice.logging;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.Map;

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

import net.minidev.json.JSONObject;


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
       
        
        Map<String,String> headersMap = (Map<String,String>)headers;
        headersMap.remove("content-length");
        headersMap.remove("x-b3-spanid");
        headersMap.remove("x-b3-parentspanid");
        Object[] args = pjp.getArgs();
        args[0] = headersMap;

        String reqBodyString = mapper.writeValueAsString(body);
        final long startTime = System.currentTimeMillis();

        Object object = pjp.proceed(args);

        String respBodyString = mapper.writeValueAsString(object);

        String requestLog = "Request = ";
        requestLog=requestLog.concat("Headers: ").concat(JSONObject.escape(mapper.writeValueAsString(headers)));
        requestLog=requestLog.concat(",Method: ").concat(request.getMethod());
        requestLog=requestLog.concat(",URI: ").concat(request.getRequestURI());
        if(!request.getMethod().equals("GET") && !request.getMethod().equals("DELETE")){
            requestLog = requestLog.concat(",Body: ").concat(JSONObject.escape(reqBodyString));
        }
        String responseLog=",Response = ";
        Long time = System.currentTimeMillis()-startTime;
        responseLog=responseLog.concat("Time: ").concat(Long.toString(time));
        responseLog=responseLog.concat(",").concat(JSONObject.escape(respBodyString));
        log.info(requestLog+responseLog);
        return object;
    }

    @Around("controllerWithOnlyHeaders(headers)")
    public Object logForRequestsWithOnlyHeaders(ProceedingJoinPoint pjp, Object headers) throws Exception,Throwable{
        
        ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String,String> headersMap = (Map<String,String>)headers;
        headersMap.remove("content-length");
        headersMap.remove("x-b3-spanid");
        headersMap.remove("x-b3-parentspanid");
        Object[] args = pjp.getArgs();
        args[0] = headersMap;

        Object object = pjp.proceed(args);
        final long startTime = System.currentTimeMillis();

        String respBodyString = mapper.writeValueAsString(object);

        String requestLog = "Request = ";
        requestLog=requestLog.concat("Headers: ").concat(JSONObject.escape(mapper.writeValueAsString(headers)));
        requestLog=requestLog.concat(",Method: ").concat(request.getMethod());
        requestLog=requestLog.concat(",URI: ").concat(request.getRequestURI());

        String responseLog=",Response = ";
        Long time = System.currentTimeMillis()-startTime;
        responseLog=responseLog.concat("Time: ").concat(Long.toString(time)).concat("ms");
        responseLog=responseLog.concat(",").concat(JSONObject.escape(respBodyString));
        log.info(requestLog+responseLog);
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