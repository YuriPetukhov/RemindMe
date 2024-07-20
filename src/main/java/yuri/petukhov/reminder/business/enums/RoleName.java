package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_USER(1),
    ROLE_STUDENT(2),
    ROLE_TEACHER(3),
    ROLE_BLOCKED(4),
    ROLE_ADMIN(5);

    private final int priority;

    RoleName(int priority) {
        this.priority = priority;
    }

}

