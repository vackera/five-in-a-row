package gzs.fiar.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidGameID extends RuntimeException {

    private final String gameID;
}
