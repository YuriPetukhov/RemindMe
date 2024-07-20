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
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.service.CardSetService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card-sets")
@Tag(name = "CARD-SET")
@Slf4j
public class CardSetController {
    private final CardSetService cardSetService;

    @PostMapping
    @Operation(summary = "Добавление набора карточек")
    public ResponseEntity<Void> createCardSet(
            @RequestBody CreateCardSetDTO cardSet,
            Authentication authentication) {
        cardSetService.createCardSet(cardSet, Long.valueOf(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{cardSetId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardSetServiceImpl.isAuthor(authentication.getName(), #cardSetId)")
    @Operation(summary = "Добавление списка карточек в набор")
    public ResponseEntity<Void> addCardsToSet(
            @PathVariable Long cardSetId,
            @RequestBody List<Long> cardIds) {
        cardSetService.addCardsToSet(cardSetId, cardIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{cardSetId}/{cardId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardSetServiceImpl.isAuthor(authentication.getName(), #cardSetId)")
    @Operation(summary = "Удаление карточки из набора")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardSetId,
            @PathVariable Long cardId) {
        cardSetService.removeCardFromSet(cardSetId, cardId);
        return ResponseEntity.noContent().build();
    }

}
