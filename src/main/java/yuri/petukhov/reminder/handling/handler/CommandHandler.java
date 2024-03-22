package yuri.petukhov.reminder.handling.handler;

import yuri.petukhov.reminder.handling.entity.CommandEntity;

public interface CommandHandler {
    void handle(CommandEntity commandEntity);
}
