package gzs.fiar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "level")
    private int level;

    @Column(name = "played")
    private LocalDateTime datePlayed;

    @Column(name = "name")
    private String playerName;

    @Column(name = "steps")
    private int winnerStepCount;

    @Column(name = "game_time")
    private long winnerTime;

    @Column(name = "score")
    private float score;
}
