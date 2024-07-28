package yuri.petukhov.reminder.business.dto;

import lombok.Data;

@Data
public class CardSetDTO {

    private Long id;
    private String setName;
    private String setDescription;
    private int setSize;
    private boolean isActive;

}
