package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    NEW,
    FREE,
    RESTRICTED,
    BLOCKED,
    PAID,
    ADMIN;
}
