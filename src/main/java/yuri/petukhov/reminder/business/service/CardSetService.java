package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.CardSet;

import java.util.List;

public interface CardSetService {
    void createCardSet(CreateCardSetDTO cardSet, Long userId);

    void addCardsToSet(Long cardSetId, List<Long> cardIds);

    CardSet removeCardFromSet(Long cardSetId, Long cardId);

    void deleteCardSet(Long cardSetId);

    List<CardSet> getAllCardSets(Long userId);

    CardSet getCardSetById(Long cardSetId);

    CardSet updateCardSet(Long cardSetId, String setName, String setDescription, List<Card> cards);

    List<CardSetDTO> getAllCardSetsByUserId(Long aLong);

    void save(CardSet cardSet);
}
