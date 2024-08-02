package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CreateGroupDTO;

import java.util.List;

public interface StudyGroupService {
    void createGroup(CreateGroupDTO groupDTO, Long aLong);

    List<CreateGroupDTO> getAllGroupsByUserId(Long aLong);
}
