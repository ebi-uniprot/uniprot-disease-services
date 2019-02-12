package uk.ac.ebi.uniprot.ds.rest.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Order(1)
@Component
@ConditionalOnExpression("${endpoint.aspect.enabled:true}")
public class RestEndpointAspect {
    @Autowired(required = false)
    private HttpServletRequest request;

    @Before("within(uk.ac.ebi.uniprot.ds.rest.controller..*)")
    public void endpointBefore(JoinPoint p) {
        log.info("HTTP Method: {}", this.request.getMethod());
        log.info("Request URI: {}", this.request.getRequestURI());
        log.info("Remote Address: {}", this.request.getRemoteAddr());
        log.info("Controller Name: {}", p.getTarget().getClass().getSimpleName());
        log.info("Method Name: {}", p.getSignature().getName());
        Object[] signatureArgs = p.getArgs();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            if (signatureArgs[0] != null) {
                log.info("Request: \n {}", mapper.writeValueAsString(signatureArgs[0]));
            }
        } catch (JsonProcessingException e) {
            // ignore
        }
    }

    @AfterReturning(value = ("within(uk.ac.ebi.uniprot.ds.rest.controller..*)"), returning = "returnValue")
    public void endpointAfterReturning(JoinPoint p, Object returnValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            log.info("Response: \n {}", mapper.writeValueAsString(returnValue));
        } catch (JsonProcessingException e) {
            // ignore
        }
        log.info(p.getTarget().getClass().getSimpleName() + " " + p.getSignature().getName() + " END");
    }


    @AfterThrowing(pointcut = ("within(uk.ac.ebi.uniprot.ds.rest.controller..*)"), throwing = "e")
    public void endpointAfterThrowing(JoinPoint p, Exception e) {
        log.error("Class Name: {}", p.getTarget().getClass().getSimpleName());
        log.error("Method Name: {}", p.getSignature().getName());
        log.error("Error Message: {}", e.getMessage());
    }
}