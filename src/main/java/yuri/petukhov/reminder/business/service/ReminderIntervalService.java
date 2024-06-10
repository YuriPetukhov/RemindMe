package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;

public interface ReminderIntervalService {
    ReminderInterval updateCurrentReminderInterval(Card card);
}
