package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.User;

import java.util.List;

public interface AdminService {
    List<User> findAdmins();

    void adminNotify(String userName, Long chatId);

    List<Integer> getStatsForAllIntervals();

    int getAllCardsNumberByReminderInterval(ReminderInterval interval);

    List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport();
}
