package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuri.petukhov.reminder.business.service.TeacherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
@Tag(name = "TEACHER")
@PreAuthorize(value = "hasRole('ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class TeacherController {

    private final TeacherService teacherService;
}
