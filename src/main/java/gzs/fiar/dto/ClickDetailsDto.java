package gzs.fiar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickDetailsDto implements Serializable {

    private String link;
    private String screenWidth;
    private String screenHeight;
}