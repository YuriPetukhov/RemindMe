package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum UserCardInputState {
    NONE,
    WEB,
    INPUT,
    STUDENT,
    STUDENT_FIRSTNAME,
    STUDENT_LASTNAME,
    WORD,
    MEANING,
    ANSWER,
    ANSWERED;
}
