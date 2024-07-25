package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CardUploadServiceImpl implements CardUploadService {

    private final UserService userService;
    private final CardService cardService;
    private final CardSetService cardSetService;
    public void addUploadCards(MultipartFile file, Long userId) {
        try {
            List<Card> cards = parseFile(file);
            saveCards(cards, userId);
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
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
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

        Iterator<Row> rows = sheet.iterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (row.getRowNum() == 0) {
                continue;
            }

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

    private void saveCards(List<Card> cards, Long userId) {
        User user = userService.findUserById(userId);
        List<Card> cardToSave = new ArrayList<>();
        for (Card card : cards) {
            card.setUser(user);
            cardToSave.add(card);
        }
        List <Card> savedCards = cardService.saveAll(cardToSave);
        CardSet cardSet = new CardSet();
        cardSet.setUser(user);
        cardSet.setCards(savedCards);
        cardSet.setSetName("New Set");
        cardSet.setSetDescription("Set Description");
        cardSet.setSetName("New Set");
        cardSetService.save(cardSet);
    }
}
