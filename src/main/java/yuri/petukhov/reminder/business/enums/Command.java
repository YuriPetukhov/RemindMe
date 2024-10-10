package yuri.petukhov.reminder.business.enums;

import lombok.Getter;

@Getter
public enum Command {
    START("/start"),
    WEB("/web"),
    ADD("/add"),
    TEACHER("/teacher"),
    STUDENT("/student");

    private final String name;

    Command(String name) {
        this.name = name;
    }

}
