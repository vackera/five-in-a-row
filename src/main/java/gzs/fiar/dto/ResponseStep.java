package gzs.fiar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import gzs.fiar.logic.GameStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStep {

    private GameStatus gameStatus;
    private StepDetails step;
    private int stepCount;
    private long gameTimeInMillis;
    private float score;
    private List<StepDetails> winnerCoordinates;
    private long ownResultId;
}
