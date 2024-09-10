package gzs.fiar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerStatusDto {

    private ZonedDateTime serverStartTime;
    private Duration serverUptime;
    private long gameCreated;
    private long gameStarted;
    private long gameFinished;
    private long activeGames;
    private long playerWon;
    private long aiWon;
    private long draw;
}
