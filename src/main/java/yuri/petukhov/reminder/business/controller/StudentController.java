package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
@Tag(name = "STUDENT")
@PreAuthorize(value = "hasRole('ROLE_ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class StudentController {
}
