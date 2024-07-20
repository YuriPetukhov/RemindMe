package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.exception.CardNotFoundException;
import yuri.petukhov.reminder.business.exception.CardSetNotFoundException;
import yuri.petukhov.reminder.business.mapper.CardSetMapper;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.CardSet;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.CardSetRepository;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.CardSetService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardSetServiceImpl implements CardSetService {
    private final CardSetRepository cardSetRepository;
    private final CardService cardService;
    private final UserService userService;
    private final CardSetMapper cardSetMapper;

    @Override
    public void createCardSet(CreateCardSetDTO createCardSetDTO, Long userId) {
        User user = userService.findUserById(userId);
        CardSet cardSet = cardSetMapper.toEntityCardSet(createCardSetDTO);
        cardSet.setUser(user);
        cardSet.setCards(new ArrayList<>());
        cardSetRepository.save(cardSet);
    }

    @Override
    public void addCardsToSet(Long cardSetId, List<Long> cardIds) {
        CardSet cardSet = getCardSetById(cardSetId);
        for (Long cardId : cardIds) {
            try {
                Card card = cardService.findById(cardId);  // Этот метод может выбросить исключение
                cardSet.getCards().add(card);
            } catch (CardNotFoundException e) {
                log.warn("Card with ID {} not found, skipping.", cardId);
            }
        }
        cardSetRepository.save(cardSet);
    }


    @Override
    public CardSet removeCardFromSet(Long cardSetId, Long cardId) {
        CardSet cardSet = getCardSetById(cardSetId);

        Card card = cardService.findById(cardId);

        if (!cardSet.getCards().contains(card)) {
            throw new CardNotFoundException("Card " + cardId + " not found in card set " + cardSetId);
        }

        cardSet.getCards().remove(card);
        return cardSetRepository.save(cardSet);
    }


    @Override
    public void deleteCardSet(Long cardSetId) {
        cardSetRepository.deleteById(cardSetId);
    }

    @Override
    public List<CardSet> getAllCardSets(Long userId) {
        return cardSetRepository.findAllByUserId(userId);
    }

    @Override
    public CardSet getCardSetById(Long cardSetId) {
        Optional<CardSet> optCardSet = cardSetRepository.findById(cardSetId);
        if (optCardSet.isPresent()) {
            return optCardSet.get();
        } else {
            throw new CardSetNotFoundException("Card set " + cardSetId + " not found");
        }
    }

    @Override
    public CardSet updateCardSet(Long cardSetId, String setName, String setDescription, List<Card> cards) {
        CardSet cardSet = getCardSetById(cardSetId);
        cardSet.setSetName(setName);
        cardSet.setSetDescription(setDescription);
        cardSet.setCards(cards);
        return cardSetRepository.save(cardSet);

    }
}
