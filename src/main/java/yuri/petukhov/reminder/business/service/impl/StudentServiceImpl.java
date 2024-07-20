package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.service.StudentService;
import yuri.petukhov.reminder.business.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final UserService userService;
}
