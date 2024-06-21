package yuri.petukhov.reminder.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.service.CardService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final CardService cardService;

    @GetMapping("/test")
    @PreAuthorize(value = "hasRole('ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
    public String showTestPage(@RequestParam("userId") Long userId, Model model, Authentication authentication) {
        model.addAttribute("userId", userId);
        return "index.html";
    }
}
