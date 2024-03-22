package yuri.petukhov.reminder.business.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.service.RecallService;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecallScheduler {
    private final RecallService recallService;

    @Scheduled(cron = "0 0/5 * * * *")
    public void activateWordsRecallMode() {
        log.info("activateRecallMode is started");
        recallService.activateRecallMode();
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void recallWords() {
        log.info("recallWords is started");
        recallService.recallWords();
    }

}
