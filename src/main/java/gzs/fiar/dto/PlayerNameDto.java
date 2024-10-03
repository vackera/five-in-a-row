package gzs.fiar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerNameDto {

    @NotBlank(message = "Name can not be blank")
    @Size(min = 3, max = 20, message = "Name has to be 3-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Name can only contain english letters, numbers, and spaces")
    private String name;
}
