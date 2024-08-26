package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CardActivateDTO;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.business.dto.StudentStatisticDTO;

import java.util.List;

public interface StudentService {
    void findStudentGroup(CommandEntity commandEntity);

    void setStudentFirstName(CommandEntity commandEntity);

    void setStudentLastName(CommandEntity commandEntity);

    void activateCardSet(Long groupId, CardActivateDTO cardActivateDTO);

    List<StudentStatisticDTO> getStudentsStatisticByGroupId(Long groupId);
}
