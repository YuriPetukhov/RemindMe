package yuri.petukhov.reminder.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller dedicated to handling test page requests.
 * This controller is responsible for displaying the test page to users with proper authorization.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class TestController {

    /**
     * Displays the test page to the user.
     * This method checks if the user has the 'ADMIN' role or is authorized by the userServiceImpl before granting access to the test page.
     * @param userId The ID of the user requesting access to the test page.
     * @param model The model object to pass attributes to the view.
     * @param authentication The authentication object containing the user's security context.
     * @return The name of the HTML file to be displayed (user-interface.html).
     */

    @GetMapping("/test")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
    public String showTestPage(@RequestParam("userId") Long userId, Model model, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);
        model.addAttribute("userId", userId);

        return "index";
    }

}
