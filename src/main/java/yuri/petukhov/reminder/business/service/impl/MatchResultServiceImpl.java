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

@Service
@RequiredArgsConstructor
public class MatchResultServiceImpl implements MatchResultService {
    private final MatchResultRepository matchResultRepository;

    @Override
    public List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport(Long userId) {
        List<ReminderInterval> allIntervals = Arrays.asList(ReminderInterval.values());

        List<Object[]> results = matchResultRepository.findErrorsGroupedByInterval(userId);

        Map<ReminderInterval, Long> errorsMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (ReminderInterval) result[0],
                        result -> (Long) result[1],
                        (existing, replacement) -> existing
                ));

        return allIntervals.stream()
                .map(interval -> new ErrorsReportDTO(interval, errorsMap.getOrDefault(interval, 0L)))
                .sorted(Comparator.comparing(dto -> dto.getInterval().getSeconds()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ErrorsReportDTO> getCardErrorsAndIntervalsReport(Long userId, Long cardId) {
        List<ReminderInterval> allIntervals = Arrays.asList(ReminderInterval.values());

        List<Object[]> results = matchResultRepository.findErrorsByCardGroupedByInterval(cardId);

        Map<ReminderInterval, Long> errorsMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (ReminderInterval) result[0],
                        result -> (Long) result[1],
                        (existing, replacement) -> existing
                ));

        return allIntervals.stream()
                .map(interval -> new ErrorsReportDTO(interval, errorsMap.getOrDefault(interval, 0L)))
                .sorted(Comparator.comparing(dto -> dto.getInterval().getSeconds()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UnRecallWordDTO> getWordsMeaningsForInterval(Long userId, ReminderInterval interval) {
        List<Object[]> results = matchResultRepository.findWordsMeaningsForInterval(userId, interval);

        return results.stream()
                .map(result -> new UnRecallWordDTO((String) result[0], ((Number) result[1]).intValue()))
                .collect(Collectors.toList());
    }

    @Override
    public int countLastFalseAnswers(Long cardId, ReminderInterval interval) {
        return matchResultRepository.countLastFalseAnswers(cardId, interval);
    }

    @Override
    public int countRemainingRecall(Long cardId, ReminderInterval interval) {
        return matchResultRepository.countRemainingRecall(cardId, interval, interval.nextInterval());
    }

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
