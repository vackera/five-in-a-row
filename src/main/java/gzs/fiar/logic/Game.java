package gzs.fiar.logic;

import lombok.Data;
import gzs.fiar.dto.StepDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Game {

    public static final int BASIC_BOARD_HEIGHT = 15;
    public static final int BASIC_BOARD_WIDTH = 15;

    public static final int EMPTY_VALUE = 0;
    public static final int PLAYER_VALUE = 1;
    public static final int AI_VALUE = 2;

    private int boardHeight;
    private int boardWidth;

    private String id;
    private int level;
    private String playerName = "Anonymous";
    private GameStatus gameStatus;
    private int stepCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private float score;

    private int[][] board;
    private List<StepDetails> winnerCoordinates = new ArrayList<>();

    public Game() {

        boardHeight = BASIC_BOARD_HEIGHT;
        boardWidth = BASIC_BOARD_WIDTH;
        initBoard();
    }

    public Game(int boardHeight, int boardWidth) {

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        initBoard();
    }

    private void initBoard() {

        board = new int[boardHeight][boardWidth];
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                board[i][j] = EMPTY_VALUE;
            }
        }
        gameStatus = GameStatus.NOT_STARTED;
        level = 1;
        startTime = null;
        endTime = null;
        stepCount = 0;
        winnerCoordinates.clear();
    }

    public boolean setCellToPlayerOne(int coordinateY, int coordinateX) {

        if (coordinateY < 0 || coordinateY >= boardHeight
                || coordinateX < 0 || coordinateX >= boardWidth
                || board[coordinateY][coordinateX] != EMPTY_VALUE) {
            return false;
        }

        if (startTime == null) {
            gameStatus = GameStatus.IN_PROGRESS;
            startTime = LocalDateTime.now();
        }

        board[coordinateY][coordinateX] = PLAYER_VALUE;
        stepCount++;

        return true;
    }

    public boolean setCellToPlayerTwo(int coordinateY, int coordinateX) {

        if (coordinateY < 0 || coordinateY >= boardHeight
                || coordinateX < 0 || coordinateX >= boardWidth
                || board[coordinateY][coordinateX] != EMPTY_VALUE) {
            return false;
        }

        board[coordinateY][coordinateX] = AI_VALUE;

        return true;
    }

    public GameStatus getGameStatusAfterStep(PlayerType currentPlayer, StepDetails step) {

        endTime = LocalDateTime.now();
        winnerCoordinates.clear();

        int coordinateX = step.getCoordinateX();
        int coordinateY = step.getCoordinateY();
        int checkedValue = currentPlayer.equals(PlayerType.USER) ? PLAYER_VALUE : AI_VALUE;

        if (isFiveInARowHorizontally(checkedValue, coordinateY, coordinateX)
                || isFiveInARowVertically(checkedValue, coordinateY, coordinateX)
                || isFiveInARowDiagonallyLeft(checkedValue, coordinateY, coordinateX)
                || isFiveInARowDiagonallyRight(checkedValue, coordinateY, coordinateX)
        ) {
            gameStatus = currentPlayer.equals(PlayerType.USER) ? GameStatus.PLAYER_WON : GameStatus.AI_WON;
            return gameStatus;
        }

        gameStatus = GameStatus.DRAW;

        for (int[] row : board) {
            for (int field : row) {
                if (field == EMPTY_VALUE) {
                    gameStatus = GameStatus.IN_PROGRESS;
                    return gameStatus;
                }
            }
        }

        return gameStatus;
    }

    private boolean isFiveInARowHorizontally(int checkedValue, int coordinateY, int coordinateX) {

        int minLimit = Math.max(coordinateX - 4, 0);
        int rangeToBack = coordinateX - minLimit;
        int countInRow;

        for (int i = rangeToBack; i >= 0; i--) {
            countInRow = 0;
            for (int j = 0; j < 5 && (coordinateX - i + j) < boardWidth; j++) {

                if (board[coordinateY][coordinateX - i + j] != checkedValue) {
                    winnerCoordinates.clear();
                    break;
                }

                countInRow++;
                winnerCoordinates.add(new StepDetails(coordinateY, coordinateX - i + j));
                if (countInRow == 5) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFiveInARowVertically(int checkedValue, int coordinateY, int coordinateX) {

        int minLimit = Math.max(coordinateY - 4, 0);
        int rangeToBack = coordinateY - minLimit;
        int countInRow;

        for (int i = rangeToBack; i >= 0; i--) {
            countInRow = 0;
            for (int j = 0; j < 5 && (coordinateY - i + j) < boardHeight; j++) {

                if (board[coordinateY - i + j][coordinateX] != checkedValue) {
                    winnerCoordinates.clear();
                    break;
                }

                countInRow++;
                winnerCoordinates.add(new StepDetails(coordinateY - i + j, coordinateX));
                if (countInRow == 5) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFiveInARowDiagonallyLeft(int checkedValue, int coordinateY, int coordinateX) {

        int minLimitY = Math.max(coordinateY - 4, 0);
        int rangeToBackY = coordinateY - minLimitY;

        int minLimitX = Math.min(coordinateX + 4, boardWidth - 1);
        int rangeToBackX = minLimitX - coordinateX;

        int rangeToBack = Math.min(rangeToBackY, rangeToBackX);
        int countInRow;

        for (int i = rangeToBack; i >= 0; i--) {
            countInRow = 0;
            for (int j = 0; j < 5 && (coordinateY - i + j) < boardHeight && (coordinateX + i - j) >= 0; j++) {

                if (board[coordinateY - i + j][coordinateX + i - j] != checkedValue) {
                    winnerCoordinates.clear();
                    break;
                }

                countInRow++;
                winnerCoordinates.add(new StepDetails(coordinateY - i + j, coordinateX + i - j));
                if (countInRow == 5) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFiveInARowDiagonallyRight(int checkedValue, int coordinateY, int coordinateX) {

        int minLimitY = Math.max(coordinateY - 4, 0);
        int rangeToBackY = coordinateY - minLimitY;

        int minLimitX = Math.max(coordinateX - 4, 0);
        int rangeToBackX = coordinateX - minLimitX;

        int rangeToBack = Math.min(rangeToBackY, rangeToBackX);
        int countInRow;

        for (int i = rangeToBack; i >= 0; i--) {
            countInRow = 0;
            for (int j = 0; j < 5 && (coordinateY - i + j) < boardHeight && (coordinateX - i + j) < boardWidth; j++) {

                if (board[coordinateY - i + j][coordinateX - i + j] != checkedValue) {
                    winnerCoordinates.clear();
                    break;
                }

                countInRow++;
                winnerCoordinates.add(new StepDetails(coordinateY - i + j, coordinateX - i + j));
                if (countInRow == 5) {
                    return true;
                }
            }
        }

        return false;
    }

    public long getGameTime() {

        return Duration.between(startTime, endTime).toMillis();
    }

    public float getCurrentScore() {

        float currentScore = stepCount + (float) getGameTime() / 100000 * 10;
        return BigDecimal.valueOf(currentScore)
                .setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public boolean isCellEmpty(int coordinateY, int coordinateX) {

        return board[coordinateY][coordinateX] == EMPTY_VALUE;
    }
}
