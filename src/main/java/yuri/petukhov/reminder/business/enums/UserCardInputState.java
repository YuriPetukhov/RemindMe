package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum UserCardInputState {
    NONE,
    WEB,
    INPUT,
    WORD,
    MEANING,
    ANSWER,
    ANSWERED;
}
