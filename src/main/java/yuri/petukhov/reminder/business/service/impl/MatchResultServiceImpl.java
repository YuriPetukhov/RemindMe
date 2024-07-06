package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.CardRecordDTO;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.repository.MatchResultRepository;
import yuri.petukhov.reminder.business.service.MatchResultService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing match results and generating reports.
 * This class provides methods to retrieve error reports, unrecalled words, and card records.
 */

@Service
@RequiredArgsConstructor
public class MatchResultServiceImpl implements MatchResultService {
    private final MatchResultRepository matchResultRepository;

    /**
     * Retrieves a report of cards' errors and intervals for a given user.
     * @param userId The ID of the user to generate the report for.
     * @return A list of ErrorsReportDTOs representing the error statistics for each interval.
     */

    @Override
    public List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport(Long userId) {
        List<ReminderInterval> allIntervals = Arrays.asList(ReminderInterval.values());

        List<Object[]> results = matchResultRepository.findAttemptsAndErrorsGroupedByInterval(userId);

        Map<ReminderInterval, ErrorsReportDTO> reportMap = new HashMap<>();

        for (Object[] result : results) {
            ReminderInterval interval = (ReminderInterval) result[0];
            Long attemptsCount = (Long) result[1];
            Long errorsCount = (Long) result[2];
            reportMap.put(interval, new ErrorsReportDTO(interval, attemptsCount, errorsCount));
        }

        return allIntervals.stream()
                .map(interval -> reportMap.getOrDefault(interval, new ErrorsReportDTO(interval, 0L, 0L)))
                .sorted(Comparator.comparing(dto -> dto.getInterval().getSeconds()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a report of a specific card's errors and intervals for a given user.
     * @param userId The ID of the user.
     * @param cardId The ID of the card to generate the report for.
     * @return A list of ErrorsReportDTOs representing the error statistics for each interval.
     */

    @Override
    public List<ErrorsReportDTO> getCardErrorsAndIntervalsReport(Long userId, Long cardId) {
        List<ReminderInterval> allIntervals = Arrays.asList(ReminderInterval.values());

        List<Object[]> results = matchResultRepository.findAttemptsAndErrorsByCardGroupedByInterval(cardId);

        Map<ReminderInterval, ErrorsReportDTO> reportMap = new HashMap<>();

        for (Object[] result : results) {
            ReminderInterval interval = (ReminderInterval) result[0];
            Long attemptsCount = (Long) result[1];
            Long errorsCount = (Long) result[2];
            reportMap.put(interval, new ErrorsReportDTO(interval, attemptsCount, errorsCount));
        }

        return allIntervals.stream()
                .map(interval -> reportMap.getOrDefault(interval, new ErrorsReportDTO(interval, 0L, 0L)))
                .sorted(Comparator.comparing(dto -> dto.getInterval().getSeconds()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of words and their meanings that are due for recall within a specific interval.
     * @param userId The ID of the user to retrieve the words for.
     * @param interval The interval to filter the words by.
     * @return A list of UnRecallWordDTOs representing the words and meanings.
     */

    @Override
    public List<UnRecallWordDTO> getWordsMeaningsForInterval(Long userId, ReminderInterval interval) {
        List<Object[]> results = matchResultRepository.findWordsMeaningsForInterval(userId, interval);

        return results.stream()
                .map(result -> new UnRecallWordDTO((String) result[0], ((Number) result[1]).intValue()))
                .collect(Collectors.toList());
    }

    /**
     * Counts the number of false answers for a specific card within a given interval.
     * @param cardId The ID of the card to count the false answers for.
     * @param interval The interval to filter the count by.
     * @return The number of false answers.
     */

    @Override
    public int countLastFalseAnswers(Long cardId, ReminderInterval interval) {
        return matchResultRepository.countLastFalseAnswers(cardId, interval);
    }

    /**
     * Counts the remaining recalls for a specific card within a given interval.
     * @param cardId The ID of the card to count the recalls for.
     * @param interval The interval to filter the count by.
     * @return The number of remaining recalls.
     */

    @Override
    public int countRemainingRecall(Long cardId, ReminderInterval interval) {
        return matchResultRepository.countRemainingRecall(cardId, interval, interval.nextInterval());
    }

    /**
     * Retrieves the record of attempts for a specific card.
     * @param cardId The ID of the card to retrieve the record for.
     * @return A list of CardRecordDTOs representing the attempt history.
     */

    @Override
    public List<CardRecordDTO> getCardRecord(Long cardId) {
        List<Object[]> rawResults = matchResultRepository.generateCardRecord(cardId);
        List<CardRecordDTO> cardRecordDTOs = new ArrayList<>();

        for (Object[] row : rawResults) {
            LocalDateTime timestamp = ((Timestamp) row[0]).toLocalDateTime();

            ReminderInterval interval = ReminderInterval.valueOf((String) row[1]);

            boolean result = (boolean) row[2];

            CardRecordDTO dto = new CardRecordDTO();
            dto.setTimestamp(timestamp);
            dto.setInterval(interval);
            dto.setResult(result);

            cardRecordDTOs.add(dto);
        }

        return cardRecordDTOs;
    }

}
