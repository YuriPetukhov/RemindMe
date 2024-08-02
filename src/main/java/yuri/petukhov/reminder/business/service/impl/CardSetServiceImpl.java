package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateCardSetDTO;
import yuri.petukhov.reminder.business.exception.CardNotFoundException;
import yuri.petukhov.reminder.business.exception.CardSetNotFoundException;
import yuri.petukhov.reminder.business.mapper.CardMapper;
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
    private final CardMapper cardMapper;

    @Override
    public Long createCardSet(CreateCardSetDTO createCardSetDTO, Long userId) {
        User user = userService.findUserById(userId);
        CardSet cardSet = cardSetMapper.toEntityCardSet(createCardSetDTO);
        cardSet.setUser(user);
        cardSet.setCards(new ArrayList<>());
        cardSetRepository.save(cardSet);
        return cardSet.getId();
    }

    @Override
    public void addCardsToSet(Long cardSetId, List<Long> cardIds) {
        CardSet cardSet = getCardSetById(cardSetId);
        for (Long cardId : cardIds) {
            try {
                Card card = cardService.findById(cardId);
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

    @Override
    public CardSet updateCardSet(Long cardSetId, CreateCardSetDTO createCardSetDTO) {
        CardSet cardSet = getCardSetById(cardSetId);
        cardSet.setSetName(createCardSetDTO.getSetName());
        cardSet.setSetDescription(createCardSetDTO.getSetDescription());
        return cardSetRepository.save(cardSet);

    }

    @Override
    public List<CardSetDTO> getAllCardSetsByUserId(Long userId) {
        List<CardSet> cardSets = cardSetRepository.findAllByUserId(userId);
        return cardSets.stream()
                .map(cardSet -> {
                    CardSetDTO cardSetDTO = cardSetMapper.toDTOCardSet(cardSet);
                    cardSetDTO.setSetSize(cardSet.getCards().size());

                    cardSetDTO.setActive(false);

                    boolean isActive = cardSet.getCards().stream()
                            .anyMatch(card -> card.getReminderDateTime() != null);
                    cardSetDTO.setActive(isActive);

                    return cardSetDTO;
                })
                .toList();
    }


    @Override
    public void save(CardSet cardSet) {
        cardSetRepository.save(cardSet);
    }

    @Override
    public CardSet getCardSet(Long cardSetId, Long aLong) {
        Optional<CardSet> cartSetOpt =  cardSetRepository.findById(cardSetId);
        return cartSetOpt.orElse(new CardSet());
    }

    @Override
    public List<CardDTO> getCardSetCards(Long setId) {
        Optional<CardSet> cardSetOpt = cardSetRepository.findById(setId);
        if(cardSetOpt.isPresent()) {
            List<Card> cards = cardSetOpt.get().getCards();
            return cards.stream()
                    .map(cardMapper::toCardDTO)
                    .toList();
        }
        return new ArrayList<>();
    }

    public boolean isAuthorCardSet(String userId, Long cardSetId) {
        User user = userService.findUserById(Long.valueOf(userId));
        CardSet cardSet = cardSetRepository.findById(cardSetId).orElse(null);
        return user != null && cardSet != null && cardSet.getUser().getId().equals(user.getId());
    }
}
