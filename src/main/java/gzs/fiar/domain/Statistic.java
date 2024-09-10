package gzs.fiar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistic")
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level")
    private Long level;

    @Column(name = "game_created")
    private long gameCreated;

    @Column(name = "game_started")
    private long gameStarted;

    @Column(name = "game_finished")
    private long gameFinished;

    @Column(name = "player_won")
    private long playerWon;

    @Column(name = "ai_won")
    private long aiWon;

    @Column(name = "draw")
    private long draw;
}
