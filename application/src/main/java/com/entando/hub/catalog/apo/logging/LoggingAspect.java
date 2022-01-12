package com.entando.hub.catalog.apo.logging;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.entando.hub.catalog.config.ApplicationConstants;

@Aspect
public class LoggingAspect {

	 private final Logger log = LoggerFactory.getLogger(this.getClass());

	    private final Environment env;

	    public LoggingAspect(Environment env) {
	        this.env = env;
	    }

	    /**
	     * Pointcut that matches all repositories, services and Web REST endpoints.
	     */
	    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
	        " || within(@org.springframework.stereotype.Service *)" +
	        " || within(@org.springframework.web.bind.annotation.RestController *)")
	    public void springBeanPointcut() {
	        // Method is empty as this is just a Pointcut, the implementations are in the advices.
	    }

	    /**
	     * Pointcut that matches all Spring beans in the application's main packages.
	     */
	    @Pointcut("within(com.entando.hub.catalog.rest..*)")
	    public void applicationPackagePointcut() {
	        // Method is empty as this is just a Pointcut, the implementations are in the advices.
	    }
	    
	    @After("execution(* com.entando.hub.catalog.rest.*.get*(..))")
	    public void logAfterget(JoinPoint joinPoint) 
	    {
	    	if (log.isDebugEnabled()) {
	        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	        	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	            log.debug("Audit_Logging: For "+request.getMethod() + " method {}.{}() by user: "+auth.getName()+" at time:"+timeStamp, joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(),auth.getName(),auth.getPrincipal(),timeStamp, Arrays.toString(joinPoint.getArgs()));
	        }
	    	
	    	
	    }
	    
	    @After("execution(* com.entando.hub.catalog.rest.*.create*(..))" +
	    		"execution(* com.entando.hub.catalog.rest.*.update*(..))" +
	    		"execution(* com.entando.hub.catalog.rest.*.delete*(..))")
	    public void logAfterallOtherMethods(JoinPoint joinPoint) 
	    {
	    	if (log.isInfoEnabled()) {
	        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	        	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	        	log.info("Audit_Logging: For "+request.getMethod() + " method {}.{}() by user: "+auth.getName()+" at time:"+timeStamp, joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(),auth.getName(),auth.getPrincipal(),timeStamp, Arrays.toString(joinPoint.getArgs()));
	        }	
	    	else if (log.isDebugEnabled()) {
	        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	        	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	            log.debug("Audit_Logging: For "+request.getMethod() + " method {}.{}() by user: "+auth.getName()+" at time:"+timeStamp, joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(),auth.getName(),auth.getPrincipal(),timeStamp, Arrays.toString(joinPoint.getArgs()));
	        }
	    }
	    
	    /**
	     * Advice that logs methods throwing exceptions.
	     *
	     * @param joinPoint join point for advice.
	     * @param e exception.
	     */
	    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
	    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
	        if (env.acceptsProfiles(Profiles.of(ApplicationConstants.SPRING_PROFILE_DEVELOPMENT))) {
	            log.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'", joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(), e.getCause() != null? e.getCause() : "NULL", e.getMessage(), e);

	        } else {
	            log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(), e.getCause() != null? e.getCause() : "NULL");
	        }
	    }
}