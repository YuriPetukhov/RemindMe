package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;

import java.util.List;

public interface MatchResultService {
    List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport(Long userId);
}
