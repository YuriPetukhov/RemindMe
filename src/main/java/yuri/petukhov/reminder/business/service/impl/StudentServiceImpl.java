package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CardActivateDTO;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.*;
import yuri.petukhov.reminder.business.repository.StudentRepository;
import yuri.petukhov.reminder.business.service.CardSetService;
import yuri.petukhov.reminder.business.service.StudentService;
import yuri.petukhov.reminder.business.service.StudyGroupService;
import yuri.petukhov.reminder.business.service.UserService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final UserService userService;
    private final StudyGroupService studyGroupService;
    private final MenuMessageCreator menuMessageCreator;
    private final StudentRepository studentRepository;
    private final CardSetService cardSetService;

    @Transactional
    @Override
    public void findStudentGroup(CommandEntity commandEntity) {
        Optional<StudyGroup> studyGroupOpt = studyGroupService.findGroupByJoinCode(commandEntity.getMessageText());
        User user = userService.findUserById(commandEntity.getUserId());
        if (studyGroupOpt.isPresent()) {
            addNewStudent(studyGroupOpt.get(), user);
        } else {
            refuseNewStudent(user);
        }
    }

    @Override
    public void setStudentFirstName(CommandEntity commandEntity) {
        Optional<Student> studentOpt = studentRepository.findByUserId(commandEntity.getUserId());
        Student student = studentOpt.orElseGet(() -> {
            Student newStudent = new Student();
            newStudent.setUser(userService.findUserById(commandEntity.getUserId()));
            return newStudent;
        });

        student.setFirstName(commandEntity.getMessageText());
        studentRepository.save(student);

        User user = student.getUser();
        user.setCardState(UserCardInputState.STUDENT_LASTNAME);
        userService.saveUser(user);

        menuMessageCreator.createLastNameMessage(user.getChatId());
    }

    @Override
    public void setStudentLastName(CommandEntity commandEntity) {
        Optional<Student> studentOpt = studentRepository.findByUserId(commandEntity.getUserId());
        Student student = studentOpt.orElseGet(() -> {
            Student newStudent = new Student();
            newStudent.setUser(userService.findUserById(commandEntity.getUserId()));
            return newStudent;
        });

        student.setLastName(commandEntity.getMessageText());
        studentRepository.save(student);

        User user = student.getUser();
        user.setCardState(UserCardInputState.NONE);
        userService.saveUser(user);

        menuMessageCreator.createYouAddedMessage(user.getChatId(), student.getFirstName());
    }

    private void refuseNewStudent(User user) {
        user.setCardState(UserCardInputState.NONE);
        userService.saveUser(user);
        menuMessageCreator.createNoSuchGroupMessage(user.getChatId());
    }

    @Transactional
    private void addNewStudent(StudyGroup studyGroup, User user) {
        List<Student> students = studyGroup.getStudents();
        if (students == null) {
            students = new ArrayList<>();
            studyGroup.setStudents(students);
        }

        boolean studentExists = students.stream()
                .anyMatch(student -> student.getUser().equals(user));

        if (studentExists) {
            refuseAlreadyAddedStudent(user);
            return;
        }

        Optional<Student> studentOpt = studentRepository.findByUserId(user.getId());
        Student student = studentOpt.orElseGet(() -> {
            Student newStudent = new Student();
            newStudent.setUser(user);
            studentRepository.save(newStudent);
            return newStudent;
        });

        students.add(student);
        studyGroupService.save(studyGroup);

        if (student.getFirstName() == null) {
            user.setCardState(UserCardInputState.STUDENT_FIRSTNAME);
            userService.saveUser(user);
            menuMessageCreator.createFirstNameMessage(user.getChatId());
            assignStudentRole(user);
        } else {
            menuMessageCreator.createYouAddedMessage(user.getChatId(), student.getFirstName());
            user.setCardState(UserCardInputState.NONE);
            userService.saveUser(user);
        }
    }

    private void refuseAlreadyAddedStudent(User user) {
        user.setCardState(UserCardInputState.NONE);
        userService.saveUser(user);
        menuMessageCreator.createAlreadyAddedMessage(user.getChatId());
    }

    private void assignStudentRole(User user) {
        userService.addRole(user.getId(), RoleName.ROLE_STUDENT);
    }

    @Override
    public void activateCardSet(Long groupId, CardActivateDTO cardActivateDTO) {
        List<Student> students;
        Optional<StudyGroup> studyGroupOpt = studyGroupService.findById(groupId);
        if (studyGroupOpt.isPresent()) {
            students = studyGroupOpt.get().getStudents();
        } else {
            return;
        }

        List<Card> cards;
        Optional<CardSet> cardSetOpt = cardSetService.findCardSetByName(cardActivateDTO.getCardSetName());
        if (cardSetOpt.isPresent()) {
            cards = cardSetOpt.get().getCards();
        } else {
            return;
        }

        for (Student student : students) {
            userService.setCardSet(student.getUser(), cards, cardActivateDTO);
        }

    }
}
