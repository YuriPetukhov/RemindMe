package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.service.MatchResultService;
import yuri.petukhov.reminder.business.service.ReminderIntervalService;

/**
 * Service implementation for managing reminder intervals for cards.
 * This class provides methods to update the current reminder interval based on the user's recall performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderIntervalServiceImpl implements ReminderIntervalService {
    private final MatchResultService matchResultService;

    /**
     * Updates the current reminder interval for a given card.
     * This method calculates the next interval based on the number of incorrect answers and remaining recalls.
     * @param card The card for which to update the interval.
     * @return The updated ReminderInterval.
     */

    @Override
    public ReminderInterval updateCurrentReminderInterval(Card card) {
        int remainingRecallCount;
        int countNextInterval = matchResultService.countLastFalseAnswers(card.getId(), card.getInterval().nextInterval());
        if (countNextInterval <= 1) {
            log.info("set the next interval because countNextInterval = {}", countNextInterval);
            return card.getInterval().nextInterval();
        } else {
            remainingRecallCount = matchResultService.countRemainingRecall(card.getId(), card.getInterval());
        }
        if (countNextInterval - remainingRecallCount == 0) {
            log.info("set the current interval because countNextInterval = {}", countNextInterval);
            return card.getInterval().nextInterval();
        } else {
            return card.getInterval();
        }
    }
}
