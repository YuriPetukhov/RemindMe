package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.service.StudyGroupService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-group")
@Tag(name = "STUDY GROUPS")
@Slf4j
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    @Operation(summary = "Добавление группы учеников")
    public ResponseEntity<Void> createGroup(
            @RequestBody CreateGroupDTO groupDTO,
            Authentication authentication) {
        log.info("new group " + groupDTO.getGroupName());
        studyGroupService.createGroup(groupDTO, Long.valueOf(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все учебные группы пользователя")
    public ResponseEntity<List<CreateGroupDTO>> getAllGroups(
            Authentication authentication) {
        return ResponseEntity.ok().body(studyGroupService.getAllGroupsByUserId(Long.valueOf(authentication.getName())));
    }
}
