package yuri.petukhov.reminder.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.model.Role;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandEntity {

    String messageText;
    Long chatId;
    Long userId;
    List<String> roles;
    UserCardInputState cardState;
}
