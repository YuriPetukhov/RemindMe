package yuri.petukhov.reminder.handling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandEntity {

    String messageText;
    Long chatId;
    Long userId;
    UserRole userRole;
    UserCardInputState cardState;
}
