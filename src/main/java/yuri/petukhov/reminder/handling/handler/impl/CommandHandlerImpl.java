package yuri.petukhov.reminder.handling.handler.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.Command;
import yuri.petukhov.reminder.business.service.InputService;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.handling.handler.CommandHandler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
@Qualifier("commandHandlerImpl")
public class CommandHandlerImpl implements CommandHandler {

    private final Map<String, CommandToRun> commandMap = new HashMap<>();
    private final InputService inputService;
    private final UserCardInputStateHandlerImpl userCardInputStateHandler;

    @FunctionalInterface
    interface CommandToRun {
        void run(CommandEntity commandEntity);
    }

    @PostConstruct
    public void init() {
        commandMap.put(Command.START.getName(), (commandEntity) -> {
            log.info("Received command /START");
            inputService.sendMenuMessage(commandEntity);
        });
        commandMap.put(Command.WEB.getName(), (commandEntity) -> {
            log.info("Received command /WEB");
            inputService.sendWebInterfaceLink(commandEntity);
        });
        commandMap.put(Command.ADD.getName(), (commandEntity) -> {
            log.info("Received command /ADD");
            inputService.createInputWordMessage(commandEntity);
        });
        commandMap.put(Command.STUDENT.getName(), (commandEntity) -> {
            log.info("Received command /STUDENT");
            inputService.promptGroupJoinCode(commandEntity);
        });
    }

    @Override
    public void handle(CommandEntity commandEntity) {
        CommandToRun commandToRun = commandMap.get(commandEntity.getMessageText().toLowerCase());
        if (commandToRun != null) {
            commandToRun.run(commandEntity);
        } else {
            log.info("No command handler was detected");
            userCardInputStateHandler.handle(commandEntity);
        }
    }
}
