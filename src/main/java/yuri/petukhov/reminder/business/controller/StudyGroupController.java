package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuri.petukhov.reminder.business.service.StudyGroupService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-group")
@Tag(name = "LOGIN")
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
}
