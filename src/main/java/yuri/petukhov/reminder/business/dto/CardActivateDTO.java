package yuri.petukhov.reminder.business.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardActivateDTO {

    private String cardSetName;
    private LocalDateTime activationStart;
    private int cardsPerBatch;
    private int activationInterval;
    private String intervalUnit;

}
