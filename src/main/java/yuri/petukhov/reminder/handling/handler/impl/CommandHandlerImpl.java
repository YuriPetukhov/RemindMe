package yuri.petukhov.reminder.handling.handler.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.service.InputService;
import yuri.petukhov.reminder.business.service.RecallService;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.handling.handler.CommandHandler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class CommandHandlerImpl implements CommandHandler {

    private final Map<UserCardInputState, Command> commandMap = new HashMap<>();
    private final InputService inputService;
    private final RecallService recallService;
    @FunctionalInterface
    interface Command {
        void run(CommandEntity commandEntity);
    }

    @PostConstruct
    public void init() {
        commandMap.put(UserCardInputState.NONE, (commandEntity) -> {
            log.info("Received command from NONE user");
            if (commandEntity.getMessageText().equals("/WEB")) {
                inputService.sendWebInterfaceLink(commandEntity);
            } else if (commandEntity.getMessageText().equals("/ADD")) {
                inputService.createInputWordMessage(commandEntity);
            } else {
                inputService.sendMenuMessage(commandEntity);
            }
        });
        commandMap.put(UserCardInputState.WORD, (commandEntity) -> {
            log.info("Received command from WORD user");
            inputService.addNameToNewCard(commandEntity);
        });
        commandMap.put(UserCardInputState.MEANING, (commandEntity) -> {
            log.info("Received command from MEANING user");
            inputService.addMeaningToNewCard(commandEntity);
        });
        commandMap.put(UserCardInputState.ANSWER, (commandEntity) -> {
            log.info("Received command ANSWER user");
            inputService.processMessage(commandEntity);
        });
    }

    @Override
    public void handle(CommandEntity commandEntity) {
        Command commandToRun = commandMap.get(commandEntity.getCardState());
        if (commandToRun != null) {
            commandToRun.run(commandEntity);
        } else {
            log.info("No command handler was detected");
        }
    }
}
