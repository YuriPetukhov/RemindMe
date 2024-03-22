package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum UserCardInputState {
    NONE,
    WORD,
    MEANING,
    ANSWER,
    ANSWERED;
}
