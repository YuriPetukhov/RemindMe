package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CardMonitoring;
import yuri.petukhov.reminder.business.dto.CommandEntity;

public interface InputService {
    void addNameToNewCard(CommandEntity commandEntity);

    void addMeaningToNewCard(CommandEntity commandEntity);

    void createInputWordMessage(CommandEntity commandEntity);

    CardMonitoring processMessage(CommandEntity commandEntity);

    void sendMenuMessage(CommandEntity commandEntity);

    void sendWebInterfaceLink(CommandEntity commandEntity);

    void response(String response, Long aLong);

    void promptGroupJoinCode(CommandEntity commandEntity);

    void createTeacherRoleAddedMessage(CommandEntity commandEntity);
}
