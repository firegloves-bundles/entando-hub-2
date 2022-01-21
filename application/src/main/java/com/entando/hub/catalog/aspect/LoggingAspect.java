package com.entando.hub.catalog.aspect;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.entando.hub.catalog.config.ApplicationConstants;

@Aspect
@Component
public class LoggingAspect {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
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

	@After("execution(* com.entando.hub.catalog.rest.*.*(..))")
	public void logAfterallOtherMethods(JoinPoint joinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String timeStamp = new SimpleDateFormat(DATE_FORMAT_NOW).format(Calendar.getInstance().getTime());
		if (env.acceptsProfiles(Profiles.of(ApplicationConstants.SPRING_PROFILE_DEVELOPMENT))) {
			if (log.isInfoEnabled()) {

				log.info(request.getMethod() + " method {}.{}() by user: " + auth.getName() + " at time:" + timeStamp, joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), auth.getName(), auth.getPrincipal(), timeStamp, Arrays.toString(joinPoint.getArgs()));
			}
			if (log.isDebugEnabled()) {
				log.debug(request.getMethod() + " method {}.{}() by user: " + auth.getName() + " at time:" + timeStamp, joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), auth.getName(), auth.getPrincipal(), timeStamp, Arrays.toString(joinPoint.getArgs()));
			}
		} else {
			if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
				if (log.isInfoEnabled()) {
					log.info(request.getMethod() + " method {}.{}() by user: " + auth.getName() + " at time:" + timeStamp,
							joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
							auth.getName(), auth.getPrincipal(), timeStamp, Arrays.toString(joinPoint.getArgs()));
				}
			}
		}
	}

	/**
	 * Advice that logs methods throwing exceptions.
	 *
	 * @param joinPoint join point for advice.
	 * @param e         exception.
	 */
	@AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		if (env.acceptsProfiles(Profiles.of(ApplicationConstants.SPRING_PROFILE_DEVELOPMENT))) {
			log.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
					e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
		} else {
			log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
		}
	}

	/**
	 * Advice that logs when a method is entered and exited.
	 *
	 * @param joinPoint join point for advice
	 * @return result
	 * @throws Throwable throws IllegalArgumentException
	 */
	@Around("applicationPackagePointcut() && springBeanPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
					joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
		}
		try {
			Object result = joinPoint.proceed();
			if (log.isDebugEnabled()) {
				log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), result);
			}
			return result;
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
					joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

			throw e;
		}
	}
}
