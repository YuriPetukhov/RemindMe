package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CardActivateDTO;
import yuri.petukhov.reminder.business.dto.CommandEntity;

public interface StudentService {
    void findStudentGroup(CommandEntity commandEntity);

    void setStudentFirstName(CommandEntity commandEntity);

    void setStudentLastName(CommandEntity commandEntity);

    void activateCardSet(Long groupId, CardActivateDTO cardActivateDTO);
}
