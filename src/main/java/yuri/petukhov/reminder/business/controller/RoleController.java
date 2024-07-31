package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "ROLES")
@Slf4j
public class RoleController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @GetMapping("/notify-role-change")
    public void notifyRoleChange() {
        messagingTemplate.convertAndSend("/topic/roles", "Roles updated");
    }


    @GetMapping("/user-roles")
    public ResponseEntity<List<String>> getUserRoles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        List<String> roles = userService.getCurrentUserRoles(Long.parseLong(username));
        log.info("roles: " + roles.toString());

        return ResponseEntity.ok(roles);
    }

}
