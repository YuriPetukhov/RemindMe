package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.handling.entity.CommandEntity;

public interface InputService {
    void addNameToNewCard(CommandEntity commandEntity);

    void addMeaningToNewCard(CommandEntity commandEntity);

    void createInputWordMessage(CommandEntity commandEntity);

    void processMessage(CommandEntity commandEntity);
}
