package yuri.petukhov.reminder.business.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentStatisticDTO {
    private String studentName;
    private List<ErrorsReportDTO> errorsReportDTO;
    private List<Integer> cards;
}
