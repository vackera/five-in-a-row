package gzs.fiar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int coordinateY;
    private int coordinateX;
}