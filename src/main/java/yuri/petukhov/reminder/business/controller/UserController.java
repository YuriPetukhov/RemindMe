package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.service.UserService;

@RestController
@RequiredArgsConstructor
@PreAuthorize(value = "hasRole('ADMIN')")
@RequestMapping("/users")
@Tag(name = "USERS")
public class UserController {

    private final UserService userService;

    @PostMapping("/{userId}/roles")
    @Operation(summary = "Добавление роли пользователю")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long userId,
            @RequestBody RoleName roleName) {
        userService.addRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/{userId}/{roleId}")
    @Operation(summary = "Удаление роли у пользователя")
    public ResponseEntity<Void> deleteRoleByUser(
            @PathVariable Long userId,
            @PathVariable RoleName roleName) {
        userService.deleteRoleByUser(userId, roleName);
        return ResponseEntity.noContent().build();
    }
}
