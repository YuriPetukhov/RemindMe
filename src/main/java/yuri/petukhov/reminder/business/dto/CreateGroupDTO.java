package yuri.petukhov.reminder.business.dto;

import lombok.Data;

@Data
public class CreateGroupDTO {

    private Long groupId;
    private String groupName;
    private String groupDescription;
    private int groupSize;
    private String joinCode;
}
