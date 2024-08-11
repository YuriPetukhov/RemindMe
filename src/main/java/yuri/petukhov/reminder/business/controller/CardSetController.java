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
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.model.CardSet;
import yuri.petukhov.reminder.business.service.CardSetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card-sets")
@Tag(name = "CARD-SET")
@Slf4j
public class CardSetController {
    private final CardSetService cardSetService;

    @PostMapping
    @Operation(summary = "Добавление набора карточек")
    public ResponseEntity<Map<String, Object>> createCardSet(
            @RequestBody CreateCardSetDTO cardSet,
            Authentication authentication) {
        log.info("new card set " + cardSet.getSetName());
        Long cardSetId = cardSetService.createCardSet(cardSet, Long.valueOf(authentication.getName()));
        Map<String, Object> response = new HashMap<>();
        response.put("cardSetId", cardSetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/cards/{cardSetId}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @cardSetServiceImpl.isAuthorCardSet(authentication.getName(), #cardSetId)")
    @Operation(summary = "Добавление списка карточек в набор")
    public ResponseEntity<Void> addCardsToSet(
            @PathVariable Long cardSetId,
            @RequestBody List<Long> cardIds) {
        cardSetService.addCardsToSet(cardSetId, cardIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{cardSetId}/{cardId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardSetServiceImpl.isAuthorCardSet(authentication.getName(), #cardSetId)")
    @Operation(summary = "Удаление карточки из набора")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardSetId,
            @PathVariable Long cardId) {
        cardSetService.removeCardFromSet(cardSetId, cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все наборы карточек пользователя")
    public ResponseEntity<List<CardSetDTO>> getAllCardSets(
            Authentication authentication) {
        return ResponseEntity.ok().body(cardSetService.getAllCardSetsByUserId(Long.valueOf(authentication.getName())));
    }

    @GetMapping("/{setId}")
    @Operation(summary = "Получить набор карточек пользователя")
    public ResponseEntity<CardSet> getCardSet(
            @PathVariable Long setId,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardSetService.getCardSet(setId, Long.valueOf(authentication.getName())));
    }

    @GetMapping("/{setId}/cards")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardSetServiceImpl.isAuthorCardSet(authentication.getName(), #cardSetId)")
    @Operation(summary = "Получить карточки набора карточек пользователя")
    public ResponseEntity<List<CardDTO>> getCardSetCards(
            @PathVariable Long setId,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardSetService.getCardSetCards(setId));
    }

    @PutMapping("/{setId}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @cardSetServiceImpl.isAuthorCardSet(authentication.getName(), #cardSetId)")
    @Operation(summary = "Изменение набора карточек")
    public ResponseEntity<Void> updateCardSet(
            @PathVariable Long setId, @RequestBody CreateCardSetDTO createCardSetDTO) {
        cardSetService.updateCardSet(setId, createCardSetDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
