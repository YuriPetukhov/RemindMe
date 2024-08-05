package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.dto.GroupDTO;
import yuri.petukhov.reminder.business.model.StudyGroup;

import java.util.List;
import java.util.Optional;

public interface StudyGroupService {
    void createGroup(CreateGroupDTO groupDTO, Long aLong);

    List<CreateGroupDTO> getAllGroupsByUserId(Long aLong);

    Optional<StudyGroup> findGroupByJoinCode(String messageText);

    void save(StudyGroup studyGroup);

    GroupDTO getGroupInfo(Long groupId);
}
