package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.repository.MatchResultRepository;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.AdminService;
import yuri.petukhov.reminder.business.service.MetricService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

import java.util.*;
import java.util.stream.Collectors;

import static yuri.petukhov.reminder.business.enums.CardActivity.FINISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final MenuMessageCreator menuMessageCreator;
    private final MetricService metricService;
    private final CardRepository cardRepository;
    private final MatchResultRepository matchResultRepository;

    @Override
    public List<User> findAdmins() {
        return userRepository.findUsersByRoleName(RoleName.ROLE_ADMIN);
    }

    @Override
    public void adminNotify(String userName, Long userChatId) {
        List<User> admins = findAdmins();
        if(admins.isEmpty()) {
            return;
        }
        for (User admin : admins) {
            if (admin.getCardState().equals(UserCardInputState.NONE)) {
                menuMessageCreator.createAdminNewUserNotifyMessage(userName, userChatId, admin.getChatId());
            } else {
                metricService.saveMetric(userName, userChatId, admin.getChatId());
            }
        }
    }

    @Override
    public List<Integer> getStatsForAllIntervals() {
        List<Integer> stats = new ArrayList<>();
        for (ReminderInterval interval : ReminderInterval.values()) {
            int cardsNumber = getAllCardsNumberByReminderInterval(interval);
            stats.add(cardsNumber);
        }
        int allCardsNumber = cardRepository.getAllCardsNumber();
        int finishedCardsNumber = cardRepository.getFinishedCardsNumber(FINISHED);
        stats.add(allCardsNumber);
        stats.add(finishedCardsNumber);

        return stats;
    }

    @Override
    public int getAllCardsNumberByReminderInterval(ReminderInterval interval) {
        return cardRepository.findAllCardsNumberByReminderInterval(interval);
    }

    @Override
    public List<ErrorsReportDTO> getCardsErrorsAndIntervalsReport() {
        List<ReminderInterval> allIntervals = Arrays.asList(ReminderInterval.values());

        List<Object[]> results = matchResultRepository.findAttemptsAndErrorsGroupedByInterval();

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

}
