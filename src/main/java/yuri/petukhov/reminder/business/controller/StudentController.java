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
import yuri.petukhov.reminder.business.dto.CardActivateDTO;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.StudentStatisticDTO;
import yuri.petukhov.reminder.business.service.StudentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
@Tag(name = "STUDENT")
@Slf4j
//@PreAuthorize(value = "hasRole('ROLE_ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/{groupId}/activate-card-set")
    @Operation(summary = "Активация набора карточек")
    public ResponseEntity<Void> createCardSet(
            @PathVariable Long groupId,
            @RequestBody CardActivateDTO cardActivateDTO,
            Authentication authentication) {
        log.info("activate card set " + cardActivateDTO.getCardSetName());
        studentService.activateCardSet(groupId, cardActivateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{groupId}/statistics")
    @Operation(summary = "Получение статистики по студентам группы")
    public ResponseEntity<List<StudentStatisticDTO>> getStudentsStatisticByGroupId(
            @PathVariable Long groupId,
            Authentication authentication) {
        log.info("get students statistics of the group " + groupId);
        return ResponseEntity.ok().body(studentService.getStudentsStatisticByGroupId(groupId));
    }
}
