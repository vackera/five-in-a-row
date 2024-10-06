package gzs.fiar.service.impl;

import gzs.fiar.dto.ResponseStep;
import gzs.fiar.dto.StepDetails;
import gzs.fiar.exception.CellAlreadyReserved;
import gzs.fiar.exception.GameAlreadyOver;
import gzs.fiar.exception.InvalidGameID;
import gzs.fiar.logic.*;
import gzs.fiar.service.GameService;
import gzs.fiar.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private final GameManager gameManager;
    private final AIPlayer aiPlayer;
    private final ResultService resultService;

    @Autowired
    public GameServiceImpl(GameManager gameManager, AIPlayer aiPlayer, ResultService resultService) {

        this.gameManager = gameManager;
        this.aiPlayer = aiPlayer;
        this.resultService = resultService;
    }

    @Override
    public String newGame(String previousGameID, String newGameID) {

        Game newGame = gameManager.newGame(newGameID);

        if (!previousGameID.isEmpty()) {

            Game previousGame = gameManager.getGame(previousGameID)
                    .orElseThrow(() -> new InvalidGameID(previousGameID));

            newGame.setPlayerName(previousGame.getPlayerName());

            gameManager.deleteGame(previousGame);
        }

        return newGame.getId();
    }

    @Override
    public void changePlayerName(String gameID, String name) {

        Game game = gameManager.getGame(gameID)
                .orElseThrow(() -> new InvalidGameID(gameID));

        game.setPlayerName(name);
    }

    @Override
    public ResponseStep playerDoStep(String gameID, StepDetails step) {

        Game game = gameManager.getGame(gameID)
                .orElseThrow(() -> new InvalidGameID(gameID));

        if (EnumSet.of(GameStatus.PLAYER_WON, GameStatus.AI_WON, GameStatus.DRAW).contains(game.getGameStatus())) {
            throw new GameAlreadyOver(gameID);
        }

        if (game.getGameStatus().equals(GameStatus.NOT_STARTED)) {
            gameManager.increaseGameStarted(game);
        }

        if (!game.setCellToPlayerOne(step.getCoordinateY(), step.getCoordinateX())) {
            throw new CellAlreadyReserved(step);
        }

        GameStatus actualGameStatus = game.getGameStatusAfterStep(PlayerType.USER, step);

        if (actualGameStatus == GameStatus.PLAYER_WON) {
            return playerWon(game, actualGameStatus);
        } else if (actualGameStatus == GameStatus.DRAW) {
            return gameIsDraw(game, actualGameStatus);
        }

        StepDetails aiStep = aiPlayer.doStep(game);

        if (!game.setCellToPlayerTwo(aiStep.getCoordinateY(), aiStep.getCoordinateX())) {
            throw new CellAlreadyReserved(step);
        }

        actualGameStatus = game.getGameStatusAfterStep(PlayerType.AI, aiStep);

        if (actualGameStatus == GameStatus.AI_WON) {
            return aiWon(game, actualGameStatus, aiStep);
        } else if (actualGameStatus == GameStatus.DRAW) {
            return gameIsDraw(game, actualGameStatus);
        }

        return new ResponseStep(actualGameStatus,
                aiStep,
                game.getStepCount(),
                game.getGameTime(),
                game.getCurrentScore(),
                List.of(),
                -1);
    }

    private ResponseStep aiWon(Game game, GameStatus actualGameStatus, StepDetails aiStep) {

        gameManager.increaseAIWins(game);
        gameManager.increaseGameFinished(game);

        return new ResponseStep(actualGameStatus,
                aiStep,
                game.getStepCount(),
                game.getGameTime(),
                game.getCurrentScore(),
                game.getWinnerCoordinates(),
                -1);
    }

    private ResponseStep gameIsDraw( Game game, GameStatus actualBoardStatus) {
        gameManager.increaseDraws(game);
        gameManager.increaseGameFinished(game);

        return new ResponseStep(actualBoardStatus,
                new StepDetails(-1, -1),
                game.getStepCount(),
                game.getGameTime(),
                game.getCurrentScore(),
                List.of(),
                -1);
    }

    private ResponseStep playerWon(Game game, GameStatus actualGameStatus) {

        long ownResultId = resultService.saveNewResult(game.getLevel(), game.getPlayerName(), game.getStepCount(), game.getGameTime(), game.getCurrentScore());

        gameManager.increasePlayerWins(game);
        gameManager.increaseGameFinished(game);

        return new ResponseStep(actualGameStatus,
                new StepDetails(-1, -1),
                game.getStepCount(),
                game.getGameTime(),
                game.getCurrentScore(),
                game.getWinnerCoordinates(),
                ownResultId);
    }

    @Override
    public String getAIVersion() {

        return AIPlayer.VERSION;
    }
}