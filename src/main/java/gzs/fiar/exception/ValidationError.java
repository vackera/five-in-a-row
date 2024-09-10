package gzs.fiar.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationError {
    private final List<CustomFieldError> fieldErrors = new ArrayList<>();

    public void addFieldError(String field, String message) {
        CustomFieldError error = new CustomFieldError(field, message);
        fieldErrors.add(error);
    }

    @Getter
    private static class CustomFieldError {

        private final String field;
        private final String message;

        CustomFieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
