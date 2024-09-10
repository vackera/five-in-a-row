package gzs.fiar.logic;

import lombok.Data;
import gzs.fiar.event.ActiveSessionListener;
import gzs.fiar.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Component
public class GameManager {

    private final StatisticService statisticService;
    private final ActiveSessionListener sessionListener;

    private final List<Game> games = new ArrayList<>();
    private long gameIDCounter;
    private long activeGamesCounter;

    @Autowired
    public GameManager(StatisticService statisticService, ActiveSessionListener sessionListener) {

        this.statisticService = statisticService;
        this.sessionListener = sessionListener;
    }

    public Game newGame(String id) {

        Game newGame = new Game();
        newGame.setId(id);
        games.add(newGame);

        statisticService.increaseGameCreated(newGame.getLevel());

        return newGame;
    }

    public Optional<Game> getGame(String gameID) {

        return games.stream()
                .filter(g -> g.getId().equals(gameID))
                .findFirst();
    }

    public void deleteGame(Game game) {

        games.remove(game);
    }

    public long getActiveGamesCounter() {

        return games.size();
    }

    public void increaseGameStarted(Game game) {

        statisticService.increaseGameStarted(game.getLevel());
    }

    public void increaseGameFinished(Game game) {

        statisticService.increaseGameFinished(game.getLevel());
    }

    public void increasePlayerWins(Game game) {

        statisticService.increasePlayerWins(game.getLevel());
    }

    public void increaseAIWins(Game game) {

        statisticService.increaseAIWins(game.getLevel());
    }

    public void increaseDraws(Game game) {

        statisticService.increaseDraws(game.getLevel());
    }

    public long deleteInactiveGames() {

        List<Game> inactiveGames = new ArrayList<>();
        long deletedGamesCount;

        for (Game game : games) {
            if (sessionListener.getActiveSessions().stream()
                    .noneMatch(sessionID -> sessionID.equals(game.getId()))) {

                inactiveGames.add(game);
            }
        }

        deletedGamesCount = inactiveGames.size();
        games.removeAll(inactiveGames);

        return deletedGamesCount;
    }

}
