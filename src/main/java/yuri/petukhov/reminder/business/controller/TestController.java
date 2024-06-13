//package yuri.petukhov.reminder.business.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import yuri.petukhov.reminder.business.dto.CardDTO;
//import yuri.petukhov.reminder.business.model.Card;
//import yuri.petukhov.reminder.business.service.CardService;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@PreAuthorize(value = "hasRole('ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
//public class TestController {
//
//    private final CardService cardService;
//
//    @GetMapping("/test")
//    public String showTestPage(@RequestParam("userId") Long userId, Model model) {
//        List<CardDTO> cards = cardService.getAllCardsDTOByUserId(userId);
//        model.addAttribute("cards", cards);
//        return "index";
//    }
//}
