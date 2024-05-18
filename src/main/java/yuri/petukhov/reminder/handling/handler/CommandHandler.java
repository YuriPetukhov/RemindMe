package yuri.petukhov.reminder.handling.handler;

import yuri.petukhov.reminder.business.dto.CommandEntity;

public interface CommandHandler {
    void handle(CommandEntity commandEntity);
}
