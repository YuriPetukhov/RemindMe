package yuri.petukhov.reminder.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.model.StudyGroup;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {

    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "groupDescription", target = "description")
    StudyGroup toEntityStudyGroup(CreateGroupDTO dto);

    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "joinCode", target = "joinCode")
    CreateGroupDTO toCreateGroupDTO(StudyGroup studyGroup);
}
