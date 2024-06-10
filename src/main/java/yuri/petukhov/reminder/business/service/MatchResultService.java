package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

import java.util.List;

public interface MatchResultService {
    List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport(Long userId);

    List<ErrorsReportDTO> getCardErrorsAndIntervalsReport(Long userId, Long cardId);

    List<UnRecallWordDTO> getWordsMeaningsForInterval(Long userId, ReminderInterval interval);

    int countLastFalseAnswers(Long id, ReminderInterval reminderInterval);

    int countRemainingRecall(Long id, ReminderInterval interval);
}
