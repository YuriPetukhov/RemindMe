package yuri.petukhov.reminder.business.enums;

import lombok.Getter;
@Getter
public enum ReminderInterval {
    MINUTES_20(20 * 60),
    HOURS_1(60 * 60),
    HOURS_4(4 * 60 * 60),
    HOURS_8(8 * 60 * 60),
    HOURS_24(24 * 60 * 60),
    HOURS_48(48 * 60 * 60),
    HOURS_96(96 * 60 * 60),
    DAYS_14(14 * 24 * 60 * 60),
    DAYS_30(30 * 24 * 60 * 60),
    DAYS_60(60 * 24 * 60 * 60);

    private final int seconds;

    ReminderInterval(int seconds) {
        this.seconds = seconds;
    }

    public ReminderInterval nextInterval() {
        ReminderInterval[] intervals = ReminderInterval.values();
        int ordinal = this.ordinal();
        return intervals[(ordinal + 1) % intervals.length];
    }
}
