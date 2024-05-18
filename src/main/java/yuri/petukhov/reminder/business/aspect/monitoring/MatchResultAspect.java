package yuri.petukhov.reminder.business.aspect.monitoring;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import yuri.petukhov.reminder.business.dto.CardMonitoring;
import yuri.petukhov.reminder.business.model.MatchResult;
import yuri.petukhov.reminder.business.repository.MatchResultRepository;
import yuri.petukhov.reminder.business.service.CardService;

@Aspect
@Component
@RequiredArgsConstructor
public class MatchResultAspect {

    private final MatchResultRepository matchResultRepository;
    private final CardService cardService;

    @Pointcut("execution(* yuri.petukhov.reminder.business.service.impl.InputServiceImpl.processMessage(..))")
    public void processMessageMethod() {}

    @AfterReturning(pointcut = "processMessageMethod()", returning = "cardMonitoring")
    public void afterProcessMessageMethod(CardMonitoring cardMonitoring) {
            MatchResult matchResult = new MatchResult(cardMonitoring.getCardId(), cardMonitoring.getInterval(), cardMonitoring.isResult());
            matchResultRepository.save(matchResult);
    }
}
