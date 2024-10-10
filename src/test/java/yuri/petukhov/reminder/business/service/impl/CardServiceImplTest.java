package yuri.petukhov.reminder.business.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static yuri.petukhov.reminder.business.enums.CardActivity.FINISHED;

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
    @DisplayName("Test getting a list of users for recall mode - successful case")
    void findUsersForRecallMode() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        List<User> expectedUsers = Arrays.asList(user1, user2, user3);

        Card card1 = Card.createCard("Word1", user1);
        Card card2 = Card.createCard("Word2", user2);
        Card card3 = Card.createCard("Word3", user3);

        List<Card> expectedCards = Arrays.asList(card1, card2, card3);

        Mockito.when(cardRepository.findDistinctRecallCardsByUserExcludingAnswer())
                .thenReturn(expectedCards);

        Mockito.when(cardRepository.findDistinctRecallCardsByUserExcludingAnswer())
                .thenReturn(expectedCards);

        List<User> actualUsers = cardService.findUsersForRecallMode();

        assertIterableEquals(expectedUsers, actualUsers);

        verify(cardRepository).findDistinctRecallCardsByUserExcludingAnswer();
    }


    @Test
    @DisplayName("Test getting a list of cards in reminder interval - successful case")
    void findCardsInReminderInterval() {
        List<Card> expectedCards = Arrays.asList(new Card(), new Card(), new Card());
        LocalDateTime recallTime = LocalDateTime.now().plusMinutes(5);

        Mockito.when(cardRepository.findAllByReminderDateTimeBeforeAndActivityNotAndRecallMode(recallTime, FINISHED, RecallMode.NONE))
                .thenReturn(expectedCards);

        List<Card> actualCards = cardService.findCardsInReminderInterval(recallTime);

        assertIterableEquals(expectedCards, actualCards);
        verify(cardRepository).findAllByReminderDateTimeBeforeAndActivityNotAndRecallMode(recallTime, FINISHED, RecallMode.NONE);
    }

    @Test
    @DisplayName("Test setting Recall Mode for cards - successful case")
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
    @DisplayName("Test setting Recall Mode for a card - successful case")
    void setRecallMode() {
        Card card = new Card();
        RecallMode recallMode = RecallMode.RECALL;

        Mockito.when(cardRepository.save(card)).thenReturn(card);

        cardService.setRecallMode(card, recallMode);

        Mockito.verify(cardRepository).save(card);

        assertEquals(recallMode, card.getRecallMode());
    }


    @Test
    @DisplayName("Test finding a card by User ID - successful case")
    void findActiveCardByUserId() {
        User user = new User();
        user.setId(1L);
        Card card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setActivity(CardActivity.ACTIVE);

        when(cardRepository.findActiveCardByUserId(user.getId())).thenReturn(Optional.of(card));

        Optional<Card> optResult = cardService.findActiveCardByUserId(user.getId());
        Card result = null;
        if (optResult.isPresent()) {
            result = optResult.get();
        }

        assertEquals(card, result);
    }

    @Test
    @DisplayName("Test setting a Card Activity - successful case")
    void setActivity() {
        Card card = new Card();
        card.setId(1L);

        cardService.setActivity(card, CardActivity.INACTIVE);

        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Test saving a card - successful case")
    void save() {
        Card card = new Card();
        card.setId(1L);

        cardService.save(card);

        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Test setting of a Reminder Interval - successful case")
    void setReminderInterval() {
        Card card = new Card();
        card.setId(1L);
        LocalDateTime time = LocalDateTime.now();
        ReminderInterval interval = ReminderInterval.DAYS_60;

        cardService.setReminderInterval(card, time, interval);

        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Test finding a card for Recall Mode - successful case")
    void findCardForRecallMode() {
        User user = new User();
        user.setId(4L);
        Card card = new Card();
        card.setId(4L);
        card.setUser(user);
        card.setRecallMode(RecallMode.RECALL);

        when(cardRepository.findRecallCardWithSmallestInterval(user.getId(), PageRequest.of(0, 1)))
                .thenReturn(Collections.singletonList(card));

        Optional<Card> foundCard = cardService.findCardForRecallMode(user.getId());

        assertTrue(foundCard.isPresent());
        assertEquals(card, foundCard.get());

        verify(cardRepository).findRecallCardWithSmallestInterval(user.getId(), PageRequest.of(0, 1));
    }


    @Test
    @DisplayName("Test deleting a card - successful case")
    void shouldDeleteCard() {
        Card card = new Card();
        card.setId(1L);

        doNothing().when(cardRepository).delete(card);

        cardService.deleteCard(card);

        verify(cardRepository).delete(card);
    }
}