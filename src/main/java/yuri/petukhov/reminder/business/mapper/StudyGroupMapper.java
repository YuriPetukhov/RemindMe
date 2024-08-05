package yuri.petukhov.reminder.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.dto.GroupDTO;
import yuri.petukhov.reminder.business.model.CardSet;
import yuri.petukhov.reminder.business.model.Student;
import yuri.petukhov.reminder.business.model.StudyGroup;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {

    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "groupDescription", target = "description")
    StudyGroup toEntityStudyGroup(CreateGroupDTO dto);

    @Mapping(source = "id", target = "groupId")
    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "joinCode", target = "joinCode")
    @Mapping(target = "groupSize", expression = "java(studyGroup.getStudents().size())")
    CreateGroupDTO toCreateGroupDTO(StudyGroup studyGroup);


    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "students", target = "students")
    @Mapping(source = "cardSets", target = "cardSets")
    @Mapping(target = "groupSize", expression = "java(studyGroup.getStudents().size())")
    GroupDTO toGroupDTO(StudyGroup studyGroup);


    default List<String> mapStudents(List<Student> students) {
        return students.stream()
                .map(student -> student.getFirstName() + " " + student.getLastName())
                .collect(Collectors.toList());
    }

    default List<String> mapCardSets(List<CardSet> cardSets) {
        return cardSets.stream()
                .map(CardSet::getSetName)
                .collect(Collectors.toList());
    }
}
