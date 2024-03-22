package yuri.petukhov.reminder.business.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @DisplayName("Test adding a new card - successful case")
    void shouldCreateANewCard() {
        User user = new User();
        String testWord = "TestWord";

        cardService.createNewCard(testWord, user);

        verify(cardRepository).save(any(Card.class));
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("Test adding a meaning to the new card - successful case")
    void shouldAddAMeaningToTheNewCard() {
        Card card = new Card();
        String testMeaning = "TestMeaning";

        cardService.addMeaningToNewCard(card, testMeaning);

        verify(cardRepository).save(any(Card.class));
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("Test getting a list of cards for recall mode - successful case")
    void findCardsForRecallMode() {
        List<Card> expectedCards = Arrays.asList(new Card(), new Card(), new Card());

        Mockito.when(cardRepository.findDistinctRecallCardsByUser())
                .thenReturn(expectedCards);

        List<Card> actualCards = cardService.findCardsForRecallMode();

        assertIterableEquals(expectedCards, actualCards);
        verify(cardRepository).findDistinctRecallCardsByUser();
    }

    @Test
    @DisplayName("Test getting a list of cards in reminder interval - successful case")
    void findCardsInReminderInterval() {
        List<Card> expectedCards = Arrays.asList(new Card(), new Card(), new Card());
        java.time.LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(20);

        Mockito.when(cardRepository.findAllByReminderDateTimeBetween(now, end))
                .thenReturn(expectedCards);

        List<Card> actualCards = cardService.findCardsInReminderInterval(now, end);

        assertIterableEquals(expectedCards, actualCards);
        verify(cardRepository).findAllByReminderDateTimeBetween(now, end);
    }

    @Test
    @DisplayName("Test setting recall mode for cards")
    void setRecallModeForList() {

        List<Card> cards = Arrays.asList(new Card(), new Card());
        RecallMode recallMode = RecallMode.RECALL;

        Mockito.when(cardRepository.saveAll(cards)).thenReturn(cards);

        cardService.setRecallMode(cards, recallMode);

        Mockito.verify(cardRepository).saveAll(cards);

        for (Card card : cards) {
            assertEquals(recallMode, card.getRecallMode());
        }
    }

    @Test
    @DisplayName("Test setting recall mode for a card")
    void setRecallMode() {
        Card card = new Card();
        RecallMode recallMode = RecallMode.WAIT;

        Mockito.when(cardRepository.save(card)).thenReturn(card);

        cardService.setRecallMode(card, recallMode);

        Mockito.verify(cardRepository).save(card);

        assertEquals(recallMode, card.getRecallMode());
    }



    @Test
    void findActiveCardByUserId() {
    }

    @Test
    void setActivity() {
    }

    @Test
    void save() {
    }

    @Test
    void setReminderInterval() {
    }

    @Test
    void findCardForRecallMode() {
    }
}