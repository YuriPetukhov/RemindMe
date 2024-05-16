package yuri.petukhov.reminder.business.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* yuri.petukhov.reminder.business.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        LOGGER.debug("Method called: {}", joinPoint.getSignature().toShortString());
        LOGGER.debug("Arguments: {}", Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* yuri.petukhov.reminder.business.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        LOGGER.debug("Method {} executed successfully", joinPoint.getSignature().toShortString());
        LOGGER.debug("Result: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* yuri.petukhov.reminder.business.controller.*.*(..))",
            throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        LOGGER.error("Error while executing method: {}", joinPoint.getSignature().toShortString());
        LOGGER.error("Exception: {}", exception.getMessage());
    }
}
