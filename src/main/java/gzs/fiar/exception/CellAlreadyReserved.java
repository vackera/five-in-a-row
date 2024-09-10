package gzs.fiar.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import gzs.fiar.dto.StepDetails;

@AllArgsConstructor
@Getter
public class CellAlreadyReserved extends RuntimeException {

    private final StepDetails stepDetails;
}
