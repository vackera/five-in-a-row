package gzs.fiar.exception;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;

@Slf4j
@NoArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.error("A validation error occurred: {}", ex.getMessage());
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        log.error("Request JSON could no be parsed: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError("JSON_PARSE_ERROR", "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);

    }

    @ExceptionHandler(CellAlreadyReserved.class)
    public ResponseEntity<ValidationError> handleCellAlreadyReserved(CellAlreadyReserved ex) {
        log.error("Cell already reserved: {} : {}", ex.getStepDetails().getCoordinateY(), ex.getStepDetails().getCoordinateX());

        ValidationError validationError = new ValidationError();
        validationError.addFieldError("coordinates", ex.getStepDetails().getCoordinateY() + " : " + ex.getStepDetails().getCoordinateX() + " - Cell already reserved");

        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidGameID.class)
    public ResponseEntity<ValidationError> handleInvalidGameID(InvalidGameID ex) {
        log.error("Invalid game ID: {}", ex.getGameID());

        ValidationError validationError = new ValidationError();
        validationError.addFieldError("gameID", ex.getGameID());

        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GameAlreadyOver.class)
    public ResponseEntity<ValidationError> handleGameAlreadyOver(GameAlreadyOver ex) {
        log.error("Game already over: {}", ex.getGameID());

        ValidationError validationError = new ValidationError();
        validationError.addFieldError("gameID", ex.getGameID());

        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError();

        for (FieldError fieldError : fieldErrors) {
            validationError.addFieldError(fieldError.getField(), messageSource.getMessage(fieldError, Locale.getDefault()));
        }

        return validationError;
    }

}
