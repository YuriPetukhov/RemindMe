package yuri.petukhov.reminder.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCardSetDTO {
    @NotBlank(message = "Set name is mandatory")
    private String setName;

    @NotBlank(message = "Set description is mandatory")
    private String setDescription;
}
