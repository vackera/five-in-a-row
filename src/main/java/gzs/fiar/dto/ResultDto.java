package gzs.fiar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResultDto {

    private long id;
    private LocalDateTime datePlayed;
    private String playerName;
    private int winnerStepCount;
    private long winnerTime;
    private float score;
}
