package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.Role;
import yuri.petukhov.reminder.business.model.Student;
import yuri.petukhov.reminder.business.model.StudyGroup;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.StudentRepository;
import yuri.petukhov.reminder.business.service.RoleService;
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
    private final RoleService roleService;
    private final StudentRepository studentRepository;

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
        Student student;
        student = studentOpt.orElseGet(Student::new);
        student.setFirstName(commandEntity.getMessageText());
        studentRepository.save(student);

        User user = userService.findUserById(commandEntity.getUserId());
        user.setCardState(UserCardInputState.STUDENT_LASTNAME);
        userService.saveUser(user);

        menuMessageCreator.createLastNameMessage(user.getChatId());
    }

    @Override
    public void setStudentLastName(CommandEntity commandEntity) {
        Optional<Student> studentOpt = studentRepository.findByUserId(commandEntity.getUserId());
        Student student;
        student = studentOpt.orElseGet(Student::new);
        student.setLastName(commandEntity.getMessageText());
        studentRepository.save(student);

        User user = userService.findUserById(commandEntity.getUserId());

        menuMessageCreator.createYouAddedMessage(user.getChatId(), student.getFirstName());

        user.setCardState(UserCardInputState.NONE);
        userService.saveUser(user);
    }

    private void refuseNewStudent(User user) {
        user.setCardState(UserCardInputState.NONE);
        userService.saveUser(user);
        menuMessageCreator.createNoSuchGroupMessage(user.getChatId());
    }

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
        Student student;
        if (studentOpt.isEmpty()) {
            student = new Student();
            student.setUser(user);
            studentRepository.save(student);
        } else {
            student = studentOpt.get();
        }

        students.add(student);
        studyGroupService.save(studyGroup);

        if (student.getFirstName() == null) {
            user.setCardState(UserCardInputState.STUDENT_FIRSTNAME);
            userService.saveUser(user);
            menuMessageCreator.createFirstNameMessage(user.getChatId());
            List<Role> roles = user.getRoles();
            Optional<Role> studentRoleOpt = roleService.findByRoleName(RoleName.ROLE_STUDENT);
            if (studentRoleOpt.isPresent()) {
                Role role = studentRoleOpt.get();
                if (!roles.contains(role)) {
                    roles.add(role);
                    userService.saveUser(user);
                }
            }
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

}

