package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.repository.CardSetRepository;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.CardSetService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardSetServiceImpl implements CardSetService {
    private final CardSetRepository cardSetRepository;
    private final CardService cardService;
}
