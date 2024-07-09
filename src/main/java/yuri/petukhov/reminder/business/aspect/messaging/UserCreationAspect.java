package yuri.petukhov.reminder.business.aspect.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yuri.petukhov.reminder.business.dto.CardMonitoring;
import yuri.petukhov.reminder.business.model.MatchResult;
import yuri.petukhov.reminder.business.producer.UserNotificationProducer;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreationAspect {

    private final UserNotificationProducer userNotificationProducer;

    @AfterReturning("execution(* yuri.petukhov.reminder.business.service.impl.UserServiceImpl.getUserRoles(..))")
    public void afterUserCreated(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long chatId = (Long) args[0];
        String userName = (String) args[1];
        userNotificationProducer.notifyAdminOfNewUser(userName, chatId);
    }

}

