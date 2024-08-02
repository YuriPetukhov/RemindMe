package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.CardSetDTO;
import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.mapper.StudyGroupMapper;
import yuri.petukhov.reminder.business.model.StudyGroup;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.StudyGroupRepository;
import yuri.petukhov.reminder.business.service.StudyGroupService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyGroupServiceImpl implements StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMapper studyGroupMapper;
    private final UserService userService;

    @Override
    public void createGroup(CreateGroupDTO groupDTO, Long userId) {
        User user = userService.findUserById(userId);
        StudyGroup group = studyGroupMapper.toEntityStudyGroup(groupDTO);
        group.setTeacher(user);
        group.setStudents(new ArrayList<>());
        group.setCardSets(new ArrayList<>());
        studyGroupRepository.save(group);
    }

    @Override
    public List<CreateGroupDTO> getAllGroupsByUserId(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findAllByTeacherId(userId);
        return studyGroups.stream()
                .map(studyGroup -> {
                    CreateGroupDTO createGroupDTO = studyGroupMapper.toCreateGroupDTO(studyGroup);
                    createGroupDTO.setGroupSize(studyGroup.getStudents().size());
                    return createGroupDTO;
                })
                .toList();
    }

    @Override
    public Optional<StudyGroup> findGroupByJoinCode(String messageText) {
        return studyGroupRepository.findByJoinCode(messageText);
    }

    @Override
    public void save(StudyGroup studyGroup) {
        studyGroupRepository.save(studyGroup);
    }
}
