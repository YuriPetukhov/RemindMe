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
