package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import yuri.petukhov.reminder.business.service.impl.PostgresUserDetailsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auto-login")
@Tag(name = "LOGIN")
public class AutoLoginController {

    private final PostgresUserDetailsService userDetailsService;

    @GetMapping
    public ResponseEntity<?> autoLogin(@RequestParam("chatId") String chatId) {

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(chatId);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(UriComponentsBuilder.fromPath("/swagger-ui.html").build().toUri());

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка аутентификации: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}
