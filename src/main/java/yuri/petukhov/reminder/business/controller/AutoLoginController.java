package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import yuri.petukhov.reminder.business.service.impl.PostgresUserDetailsService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auto-login")
@Tag(name = "LOGIN")
@Slf4j
public class AutoLoginController {

    private final PostgresUserDetailsService userDetailsService;

//    @GetMapping
//    public ResponseEntity<?> autoLogin(@RequestParam("userId") String userId) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(
//                userDetails, null, userDetails.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(UriComponentsBuilder.fromPath("/test")
//                .queryParam("userId", userDetails.getUsername())
//                .build().toUri());
//
//        return new ResponseEntity<>(headers, HttpStatus.FOUND);
//    }

    @GetMapping
    public ResponseEntity<?> autoLogin(@RequestParam("userId") String userId) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(UriComponentsBuilder.fromPath("/swagger-ui.html").build().toUri());

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
