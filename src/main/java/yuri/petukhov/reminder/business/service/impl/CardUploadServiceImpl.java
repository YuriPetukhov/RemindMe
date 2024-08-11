package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.exception.FileProcessingException;
import yuri.petukhov.reminder.business.exception.UnsupportedFileFormatException;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.CardSet;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.CardSetService;
import yuri.petukhov.reminder.business.service.CardUploadService;
import yuri.petukhov.reminder.business.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardUploadServiceImpl implements CardUploadService {

    private final UserService userService;
    private final CardService cardService;
    private final CardSetService cardSetService;

    public void addUploadCards(MultipartFile file, String activationStart, int cardsPerBatch, int activationInterval, String intervalUnit, Long userId) {
        try {
            List<Card> cards = parseFile(file);
            saveCards(cards, activationStart, cardsPerBatch, activationInterval, intervalUnit, userId);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка при обработке файла: " + e.getMessage(), e);
        }
    }

    @Override
    public void uploadSetCards(MultipartFile file, Long cardSetId) {
        try {
            List<Card> cards = parseFile(file);
            addCardsToSet(cards, cardSetId);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка при обработке файла: " + e.getMessage(), e);
        }
    }

    private List<Card> parseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.endsWith(".csv")) {
            return parseCsvFile(file);
        } else if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            return parseExcelFile(file);
        } else {
            throw new UnsupportedFileFormatException("Неподдерживаемый формат файла");
        }
    }

    private List<Card> parseCsvFile(MultipartFile file) throws IOException {
        List<Card> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Card card = new Card();
                card.setCardName(fields[0]);
                card.setCardMeaning(fields[1]);

                cards.add(card);
            }
        }
        return cards;
    }

    private List<Card> parseExcelFile(MultipartFile file) throws IOException {
        List<Card> cards = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            String title = row.getCell(0).getStringCellValue();
            String description = row.getCell(1).getStringCellValue();

            Card card = new Card();
            card.setCardName(title);
            card.setCardMeaning(description);
            cards.add(card);
        }

        workbook.close();
        return cards;
    }


    private void saveCards(List<Card> cards, String activationStart, int cardsPerBatch, int activationInterval, String intervalUnit, Long userId) {
        User user = userService.findUserById(userId);
        List<Card> cardToSave = new ArrayList<>();
        LocalDateTime currentActivationDate = null;

        if (activationStart != null && !activationStart.isBlank()) {
            currentActivationDate = LocalDateTime.parse(activationStart);
        } else {
            currentActivationDate = LocalDateTime.now();
        }

        int batchCounter = 0;

        for (Card card : cards) {
            card.setUser(user);

            if (currentActivationDate != null) {
                card.setActivity(CardActivity.INACTIVE);
                card.setRecallMode(RecallMode.NONE);
                card.setInterval(ReminderInterval.MINUTES_20);
                card.setReminderDateTime(currentActivationDate);

                batchCounter++;
                if (batchCounter >= cardsPerBatch) {
                    currentActivationDate = userService.getNextReminderDate(currentActivationDate, activationInterval, intervalUnit);
                    batchCounter = 0;
                }
            }

            cardToSave.add(card);
        }


        cardService.saveAll(cardToSave);

    }

    private void addCardsToSet(List<Card> cards, Long cardSetId) {
        CardSet cardSet = cardSetService.getCardSetById(cardSetId);
        User user = cardSet.getUser();
        List<Card> setCards = cardSet.getCards();
        for (Card card : cards) {
            card.setUser(user);
        }
        cardService.saveAll(cards);
        setCards.addAll(cards);
        cardSetService.save(cardSet);

    }

}
