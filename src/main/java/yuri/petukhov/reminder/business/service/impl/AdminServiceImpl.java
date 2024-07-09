package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.AdminService;
import yuri.petukhov.reminder.business.service.MetricService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final MenuMessageCreator menuMessageCreator;
    private final MetricService metricService;

    @Override
    public List<User> findAdmins() {
        return userRepository.findUsersByRoleName(RoleName.ROLE_ADMIN);
    }

    @Override
    public void adminNotificate(String userName, Long userChatId) {
        List<User> admins = findAdmins();
        for (User admin : admins) {
            if (admin.getCardState().equals(UserCardInputState.NONE)) {
                menuMessageCreator.createAdminNewUserNotifyMessage(userName, userChatId, admin.getChatId());
            } else {
                metricService.saveMetric(userName, userChatId, admin.getChatId());
            }
        }
    }
}
