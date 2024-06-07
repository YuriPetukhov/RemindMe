package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.repository.MatchResultRepository;
import yuri.petukhov.reminder.business.service.MatchResultService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchResultServiceImpl implements MatchResultService {
    private final MatchResultRepository matchResultRepository;

    @Override
    public List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport(Long userId) {
        List<Object[]> results = matchResultRepository.findErrorsGroupedByInterval(userId);
        return results.stream()
                .map(result -> new ErrorsReportDTO((ReminderInterval) result[0], (Long) result[1]))
                .sorted(Comparator.comparing(dto -> dto.getInterval().getSeconds()))
                .collect(Collectors.toList());
    }
}
