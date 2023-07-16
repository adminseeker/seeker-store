package com.adminseeker.authservice.logging;

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

import com.adminseeker.authservice.dto.AuthRequest;
import com.adminseeker.authservice.proxies.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;


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

        String reqBodyString = mapper.writeValueAsString(body);
        final long startTime = System.currentTimeMillis();

        Map<String,String> headersMap = (Map<String,String>)headers;
        headersMap.remove("content-length");
        headersMap.remove("x-b3-spanid");
        headersMap.remove("x-b3-parentspanid");
        Object[] args = pjp.getArgs();
        args[0] = headersMap;
        Object object = pjp.proceed(args);
        
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

        String requestLog = "Request = ";
        requestLog=requestLog.concat("Headers: ").concat(JSONObject.escape(mapper.writeValueAsString(headers)));
        requestLog=requestLog.concat(",Method: ").concat(request.getMethod());
        requestLog=requestLog.concat(",URI: ").concat(request.getRequestURI());
        if(!request.getMethod().equals("GET") && !request.getMethod().equals("DELETE")){
            requestLog = requestLog.concat(",Body: ").concat(JSONObject.escape(reqBodyString));
        }
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