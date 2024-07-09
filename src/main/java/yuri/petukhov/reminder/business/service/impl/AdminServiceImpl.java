package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.AdminService;
import yuri.petukhov.reminder.business.service.CardSetService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.List;

import static yuri.petukhov.reminder.business.enums.RoleName.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final CardSetService cardSetService;

    @Override
    public List<User> findAdmins() {
        return userRepository.findUsersByRoleName(RoleName.ROLE_ADMIN);
    }
}
