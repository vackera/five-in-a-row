package gzs.fiar.service.impl;

import gzs.fiar.dto.ResponseStep;
import gzs.fiar.dto.StepDetails;
import gzs.fiar.exception.CellAlreadyReserved;
import gzs.fiar.exception.GameAlreadyOver;
import gzs.fiar.exception.InvalidGameID;
import gzs.fiar.logic.*;
import gzs.fiar.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameManager gameManager;

    @Mock
    private AIPlayer aiPlayer;

    @Mock
    private ResultService resultService;

    private GameServiceImpl underTest;

    @BeforeEach
    void setUp() {

        underTest = new GameServiceImpl(gameManager, aiPlayer, resultService);
    }

    @Test
    void test_newGame_withoutPreviousGameID_shouldCreateNewGame() {

        String newGameID = "newGameID123";

        Game newGame = new Game();
        newGame.setId(newGameID);

        when(gameManager.newGame(newGameID)).thenReturn(newGame);

        String result = underTest.newGame("", newGameID);

        assertEquals(newGameID, result);

        verify(gameManager, never()).deleteGame(any());
    }

    @Test
    void test_newGame_withPreviousGameID_shouldCopyPlayerNameAndDeletePreviousGame() {

        //GIVEN
        String previousGameID = "oldGameID123";
        String newGameID = "newGameID879";

        Game previousGame = new Game();
        previousGame.setId(previousGameID);
        previousGame.setPlayerName("Player Name");

        Game newGame = new Game();
        newGame.setId(newGameID);

        when(gameManager.newGame(newGameID)).thenReturn(newGame);
        when(gameManager.getGame(previousGameID)).thenReturn(Optional.of(previousGame));

        //WHEN
        String result = underTest.newGame(previousGameID, newGameID);

        //THEN
        assertEquals(newGameID, result);
        assertEquals("Player Name", newGame.getPlayerName());
        verify(gameManager).deleteGame(previousGame);
    }

    @Test
    void test_newGame_withInvalidPreviousGameID_shouldThrowException() {

        //GIVEN
        String previousGameID = "invalidGameID789";
        String newGameID = "newGameID123";

        Game newGame = new Game();
        newGame.setId(newGameID);

        when(gameManager.newGame(newGameID)).thenReturn(newGame);
        when(gameManager.getGame(previousGameID)).thenReturn(Optional.empty());

        //WHEN
        Executable executable = () -> underTest.newGame(previousGameID, newGameID);

        //THEN
        assertThrows(InvalidGameID.class, executable);
    }

    @Test
    void test_changePlayerName_withValidGameID_shouldUpdatePlayerName() {

        //GIVEN
        String gameID = "validGameID123";
        String newName = "New Player Name";

        Game game = new Game();
        game.setId(gameID);
        game.setPlayerName("Old Player Name");

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));

        //WHEN
        underTest.changePlayerName(gameID, newName);

        //THEN
        assertEquals(newName, game.getPlayerName());
    }

    @Test
    void test_changePlayerName_withInvalidGameID_shouldThrowException() {

        //GIVEN
        String gameID = "invalidGameID789";
        String newName = "New Player Name";

        when(gameManager.getGame(gameID)).thenReturn(Optional.empty());

        //WHEN
        Executable executable = () -> underTest.changePlayerName(gameID, newName);

        //THEN
        assertThrows(InvalidGameID.class, executable);
    }

    @Test
    void test_getAIVersion_shouldReturnCorrectVersion() {

        //GIVEN
        String expected = AIPlayer.VERSION;

        //WHEN
        String actual = underTest.getAIVersion();

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    void test_playerDoStep_withInvalidGameID_shouldThrowException() {

        //GIVEN
        String gameID = "invalidGameID789";
        StepDetails step = new StepDetails(2, 5);

        when(gameManager.getGame(gameID)).thenReturn(Optional.empty());

        //WHEN
        Executable executable = () -> underTest.playerDoStep(gameID, step);

        //THEN
        assertThrows(InvalidGameID.class, executable);
    }

    @Test
    void test_playerDoStep_withGameAlreadyOver_shouldThrowException() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        Game game = new Game();
        game.setGameStatus(GameStatus.PLAYER_WON);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));

        //WHEN
        Executable executable = () -> underTest.playerDoStep(gameID, step);

        //THEN
        assertThrows(GameAlreadyOver.class, executable);
    }

    @Test
    void test_playerDoStep_withNotStartedGame_shouldIncreaseStartedGames() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        Game game = new Game();
        game.setGameStatus(GameStatus.NOT_STARTED);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));

        StepDetails aiStep = new StepDetails(3, 6);
        when(aiPlayer.doStep(game)).thenReturn(aiStep);

        //WHEN
        underTest.playerDoStep(gameID, step);

        //THEN
        verify(gameManager).increaseGameStarted(game);
    }

    @Test
    void test_playerDoStep_withCellAlreadyReserved_shouldThrowException() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        Game game = mock(Game.class);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));
        when(game.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);
        when(game.setCellToPlayerOne(step.getCoordinateY(), step.getCoordinateX())).thenReturn(false);

        //WHEN
        Executable executable = () -> underTest.playerDoStep(gameID, step);

        //THEN
        assertThrows(CellAlreadyReserved.class, executable);
    }

    @Test
    void test_playerDoStep_playerWon() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        Game game = mock(Game.class);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));
        when(game.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);
        when(game.setCellToPlayerOne(step.getCoordinateY(), step.getCoordinateX())).thenReturn(true);
        when(game.getGameStatusAfterStep(PlayerType.USER, step)).thenReturn(GameStatus.PLAYER_WON);

        //WHEN
        ResponseStep responseStep = underTest.playerDoStep(gameID, step);

        //THEN
        assertEquals(GameStatus.PLAYER_WON, responseStep.getGameStatus());

        verify(gameManager).increaseGameFinished(game);
        verify(gameManager).increasePlayerWins(game);
    }

    @Test
    void test_playerDoStep_aiWon() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        StepDetails aiStep = new StepDetails(3, 6);
        Game game = mock(Game.class);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));
        when(game.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);
        when(game.setCellToPlayerOne(step.getCoordinateY(), step.getCoordinateX())).thenReturn(true);
        when(game.getGameStatusAfterStep(PlayerType.USER, step)).thenReturn(GameStatus.IN_PROGRESS);

        when(aiPlayer.doStep(game)).thenReturn(new StepDetails(aiStep.getCoordinateY(), aiStep.getCoordinateX()));
        when(game.setCellToPlayerTwo(aiStep.getCoordinateY(), aiStep.getCoordinateX())).thenReturn(true);
        when(game.getGameStatusAfterStep(PlayerType.AI, aiStep)).thenReturn(GameStatus.AI_WON);

        //WHEN
        ResponseStep responseStep = underTest.playerDoStep(gameID, step);

        //THEN
        assertEquals(GameStatus.AI_WON, responseStep.getGameStatus());
        verify(gameManager).increaseGameFinished(game);
        verify(gameManager).increaseAIWins(game);
    }

    @Test
    void test_playerDoStep_draw() {

        //GIVEN
        String gameID = "gameID123";
        StepDetails step = new StepDetails(2, 5);
        Game game = mock(Game.class);

        when(gameManager.getGame(gameID)).thenReturn(Optional.of(game));
        when(game.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);
        when(game.setCellToPlayerOne(step.getCoordinateY(), step.getCoordinateX())).thenReturn(true);
        when(game.getGameStatusAfterStep(PlayerType.USER, step)).thenReturn(GameStatus.DRAW);

        //WHEN
        ResponseStep responseStep = underTest.playerDoStep(gameID, step);

        //THEN
        assertEquals(GameStatus.DRAW, responseStep.getGameStatus());
        verify(gameManager).increaseGameFinished(game);
        verify(gameManager).increaseDraws(game);
    }
}
