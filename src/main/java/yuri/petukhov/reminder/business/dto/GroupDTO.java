package yuri.petukhov.reminder.business.dto;

import lombok.Data;
import yuri.petukhov.reminder.business.model.CardSet;
import yuri.petukhov.reminder.business.model.Student;

import java.util.List;

@Data
public class GroupDTO {

    private String groupName;
    private String groupDescription;
    private int groupSize;
    private String joinCode;
    private List<String> students;
    private List<String> cardSets;
}
