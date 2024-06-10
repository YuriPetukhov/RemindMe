package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.service.MatchResultService;
import yuri.petukhov.reminder.business.service.ReminderIntervalService;

@Service
@RequiredArgsConstructor
public class ReminderIntervalServiceImpl implements ReminderIntervalService {
    private final MatchResultService matchResultService;

    @Override
    public ReminderInterval updateCurrentReminderInterval(Card card) {
        int remainingRecallCount;
        int countNextInterval = matchResultService.countLastFalseAnswers(card.getId(), card.getInterval().nextInterval());
        if (countNextInterval <= 1) {
            return card.getInterval().nextInterval();
        } else {
            remainingRecallCount = matchResultService.countRemainingRecall(card.getId(), card.getInterval());
        }
        if (countNextInterval - remainingRecallCount == 0) {
            return card.getInterval().nextInterval();
        } else {
            return card.getInterval();
        }
    }
}
