package gzs.fiar.logic;

import lombok.Getter;
import gzs.fiar.dto.StepDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Component
public class AIPlayer {

    public static final String VERSION = "1.0";
    public static final int AI_LEVELS = 1;

    private static final int[] dangerFactorDefaults = {0, 0, 20, 100, 1000, 10000};
    private static final int[] advantageFactorDefaults = {0, 0, 20, 100, 1000, 10000};

    private static final int PARTIAL_DANGER_RATIO = 20;
    private static final int PARTIAL_ADVANTAGE_RATIO = 15;

    private int[][] board;
    private int[][] dangerFactors;
    private int[][] advantageFactors;
    private int[][] optimalFactors;

    private int userFieldCount;
    private int aiFieldCount;
    private int emptyFieldCount;
    private int emptyFieldPosition = 0;

    private final StepDetails mostDangerousStep = new StepDetails();
    private final StepDetails mostAdvantageousStep = new StepDetails();
    private final StepDetails mostOptimalStep = new StepDetails();

    public StepDetails doStep(Game gameBoard) {

        return calculateBestStep(gameBoard);
    }

    private StepDetails calculateBestStep(Game gameBoard) {

        final int tableHeight = gameBoard.getBoardHeight();
        final int tableWidth = gameBoard.getBoardWidth();

        board = gameBoard.getBoard();

        dangerFactors = new int[tableHeight][tableWidth];
        advantageFactors = new int[tableHeight][tableWidth];
        optimalFactors = new int[tableHeight][tableWidth];

        analyzeTable(tableHeight, tableWidth);

        calculateMostLikelySteps(tableHeight, tableWidth);

        return getBestStep(mostAdvantageousStep, mostDangerousStep, mostOptimalStep);
    }

    private void calculateMostLikelySteps(int tableHeight, int tableWidth) {

        int mostDangerousValue = 0;
        int mostAdvantageousValue = 0;
        int mostOptimalValue = 0;
        List<StepDetails> mostOptimalSteps = new ArrayList<>();

        for (int i = 0; i < tableHeight; i++) {
            for (int j = 0; j < tableWidth; j++) {
                mostDangerousValue = compareMostDangerousValueWithCurrent(i, j, mostDangerousValue);
                mostAdvantageousValue = compareMostAdvantageousValueWithCurrent(i, j, mostAdvantageousValue);
                mostOptimalValue = compareMostOptimalValueWithCurrent(i, j, mostOptimalValue, mostOptimalSteps);
            }
        }

        int randomOfMostOptimalSteps = new Random().nextInt(mostOptimalSteps.size());
        mostOptimalStep.setCoordinateY(mostOptimalSteps.get(randomOfMostOptimalSteps).getCoordinateY());
        mostOptimalStep.setCoordinateX(mostOptimalSteps.get(randomOfMostOptimalSteps).getCoordinateX());
    }

    private int compareMostOptimalValueWithCurrent(int i, int j, int mostOptimalValue, List<StepDetails> mostOptimalSteps) {

        if (optimalFactors[i][j] > mostOptimalValue) {
            mostOptimalValue = optimalFactors[i][j];
            mostOptimalSteps.clear();
            mostOptimalSteps.add(new StepDetails(i, j));
        } else if (optimalFactors[i][j] == mostOptimalValue && mostOptimalSteps.size() < 10) {
            mostOptimalSteps.add(new StepDetails(i, j));
        }
        return mostOptimalValue;
    }

    private int compareMostAdvantageousValueWithCurrent(int i, int j, int mostAdvantageousValue) {

        if (advantageFactors[i][j] > mostAdvantageousValue) {
            mostAdvantageousValue = advantageFactors[i][j];
            mostAdvantageousStep.setCoordinateY(i);
            mostAdvantageousStep.setCoordinateX(j);
        }
        return mostAdvantageousValue;
    }

    private int compareMostDangerousValueWithCurrent(int i, int j, int mostDangerousValue) {

        if (dangerFactors[i][j] > mostDangerousValue) {
            mostDangerousValue = dangerFactors[i][j];
            mostDangerousStep.setCoordinateY(i);
            mostDangerousStep.setCoordinateX(j);
        }
        return mostDangerousValue;
    }

    private void analyzeTable(int tableHeight, int tableWidth) {

        for (int i = 0; i < tableHeight; i++) {
            for (int j = 0; j < tableWidth; j++) {
                analyzeHorizontallyPossibilities(i, tableWidth, j);
                analyzeVerticallyPossibilities(i, tableHeight, j);
                analyzeDiagonallyLeftPossibilities(i, tableHeight, j, tableWidth);
                analyzeDiagonallyRightPossibilities(i, j, tableWidth, tableHeight);
            }
        }

        calculateOptimalFactors(tableHeight, tableWidth);
    }

    private void calculateOptimalFactors(int tableHeight, int tableWidth) {

        for (int i = 0; i < tableHeight; i++) {
            for (int j = 0; j < tableWidth; j++) {
                optimalFactors[i][j] = dangerFactors[i][j] + advantageFactors[i][j];
            }
        }
    }

    private StepDetails getBestStep(StepDetails mostAdvantageousStep, StepDetails mostDangerousStep, StepDetails mostOptimalStep) {

        StepDetails bestStep;

        if (advantageFactors[mostAdvantageousStep.getCoordinateY()][mostAdvantageousStep.getCoordinateX()] >= advantageFactorDefaults[5]) {
            bestStep = mostAdvantageousStep;
        } else if (dangerFactors[mostDangerousStep.getCoordinateY()][mostDangerousStep.getCoordinateX()] >= dangerFactorDefaults[5]) {
            bestStep = mostDangerousStep;
        } else if (dangerFactors[mostDangerousStep.getCoordinateY()][mostDangerousStep.getCoordinateX()] >= dangerFactorDefaults[4]
                && advantageFactors[mostAdvantageousStep.getCoordinateY()][mostAdvantageousStep.getCoordinateX()] >= advantageFactorDefaults[4]) {
            bestStep = mostAdvantageousStep;
        } else {
            bestStep = mostOptimalStep;
        }

        return bestStep;
    }

    private void analyzeHorizontallyPossibilities(int coordinateY, int tableWidth, int coordinateX) {

        analyzeHorizontallyFiveFields(coordinateY, tableWidth, coordinateX);

        analyzeHorizontallyFourFields(coordinateY, tableWidth, coordinateX);

        analyzeHorizontallyThreeFields(coordinateY, tableWidth, coordinateX);

        analyzeHorizontallyTwoFields(coordinateY, tableWidth, coordinateX);
    }

    private void analyzeHorizontallyTwoFields(int coordinateY, int tableWidth, int coordinateX) {

        if (coordinateX < tableWidth - 1) {
            countHorizontallyCheckedFields(2, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 1) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 2, PlayerType.USER);
                }
                if (aiFieldCount == 1) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 2, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeHorizontallyThreeFields(int coordinateY, int tableWidth, int coordinateX) {

        if (coordinateX < tableWidth - 2) {
            countHorizontallyCheckedFields(3, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 2) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 3, PlayerType.USER);
                }
                if (aiFieldCount == 2) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 3, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeHorizontallyFourFields(int coordinateY, int tableWidth, int coordinateX) {

        if (coordinateX < tableWidth - 3) {
            countHorizontallyCheckedFields(4, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 3) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 4, PlayerType.USER);
                }
                if (aiFieldCount == 3) {
                    increaseFactorsWhenHorizontally(coordinateY, coordinateX, tableWidth, 4, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeHorizontallyFiveFields(int coordinateY, int tableWidth, int coordinateX) {

        if (coordinateX < tableWidth - 4) {
            countHorizontallyCheckedFields(5, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 4) {
                    dangerFactors[coordinateY][coordinateX + emptyFieldPosition] += dangerFactorDefaults[5];
                }
                if (aiFieldCount == 4) {
                    advantageFactors[coordinateY][coordinateX + emptyFieldPosition] += advantageFactorDefaults[5];
                }
            }
        }
    }

    private void increaseFactorsWhenHorizontally(int coordinateY, int coordinateX, int tableWidth, int range, PlayerType playerType) {

        JustCheckedValues basics = getJustCheckedValues(playerType);

        if (coordinateX - 1 < 0
                || coordinateX + range >= tableWidth
                || board[coordinateY][coordinateX - 1] == basics.opponentValue()
                || board[coordinateY][coordinateX + range] == basics.opponentValue()) {
            basics.factors()[coordinateY][coordinateX + emptyFieldPosition] += basics.factorValues()[range] / basics.ratio();
        } else {
            basics.factors()[coordinateY][coordinateX + emptyFieldPosition] += basics.factorValues()[range];
        }
    }

    private JustCheckedValues getJustCheckedValues(PlayerType playerType) {

        int opponentValue;
        int[][] factors;
        int[] factorValues;
        int ratio;

        if (playerType == PlayerType.USER) {
            opponentValue = Game.AI_VALUE;
            factors = dangerFactors;
            factorValues = dangerFactorDefaults;
            ratio = PARTIAL_DANGER_RATIO;
        } else {
            opponentValue = Game.PLAYER_VALUE;
            factors = advantageFactors;
            factorValues = advantageFactorDefaults;
            ratio = PARTIAL_ADVANTAGE_RATIO;
        }
        return new JustCheckedValues(opponentValue, factors, factorValues, ratio);
    }

    private record JustCheckedValues(int opponentValue, int[][] factors, int[] factorValues, int ratio) {
    }

    private void countHorizontallyCheckedFields(int range, int coordinateY, int coordinateX) {

        userFieldCount = aiFieldCount = emptyFieldCount = emptyFieldPosition = 0;
        for (int i = 0; i < range; i++) {
            if (board[coordinateY][coordinateX + i] == Game.PLAYER_VALUE) {
                userFieldCount++;
            } else if (board[coordinateY][coordinateX + i] == Game.EMPTY_VALUE) {
                emptyFieldCount++;
                emptyFieldPosition = i;
            } else {
                aiFieldCount++;
            }
        }
    }

    private void analyzeVerticallyPossibilities(int coordinateY, int tableHeight, int coordinateX) {

        analyzeVerticallyFiveFields(coordinateY, tableHeight, coordinateX);

        analyzeVerticallyFourFields(coordinateY, tableHeight, coordinateX);

        analyzeVerticallyThreeFields(coordinateY, tableHeight, coordinateX);

        analyzeVerticallyTwoFields(coordinateY, tableHeight, coordinateX);
    }

    private void analyzeVerticallyTwoFields(int coordinateY, int tableHeight, int coordinateX) {

        if (coordinateY < tableHeight - 1) {
            countVerticallyCheckedFields(2, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 1) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 2, PlayerType.USER);
                }
                if (aiFieldCount == 1) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 2, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeVerticallyThreeFields(int coordinateY, int tableHeight, int coordinateX) {

        if (coordinateY < tableHeight - 2) {
            countVerticallyCheckedFields(3, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 2) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 3, PlayerType.USER);
                }
                if (aiFieldCount == 2) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 3, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeVerticallyFourFields(int coordinateY, int tableHeight, int coordinateX) {

        if (coordinateY < tableHeight - 3) {
            countVerticallyCheckedFields(4, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 3) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 4, PlayerType.USER);
                }
                if (aiFieldCount == 3) {
                    increaseFactorsWhenVertically(coordinateY, coordinateX, tableHeight, 4, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeVerticallyFiveFields(int coordinateY, int tableHeight, int coordinateX) {

        if (coordinateY < tableHeight - 4) {
            countVerticallyCheckedFields(5, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 4) {
                    dangerFactors[coordinateY + emptyFieldPosition][coordinateX] += dangerFactorDefaults[5];
                }
                if (aiFieldCount == 4) {
                    advantageFactors[coordinateY + emptyFieldPosition][coordinateX] += advantageFactorDefaults[5];
                }
            }
        }
    }

    private void increaseFactorsWhenVertically(int coordinateY, int coordinateX, int tableHeight, int range, PlayerType playerType) {

        JustCheckedValues basics = getJustCheckedValues(playerType);

        if (coordinateY - 1 < 0
                || coordinateY + range >= tableHeight
                || board[coordinateY - 1][coordinateX] == basics.opponentValue()
                || board[coordinateY + range][coordinateX] == basics.opponentValue()) {
            basics.factors()[coordinateY + emptyFieldPosition][coordinateX] += basics.factorValues()[range] / basics.ratio();
        } else {
            basics.factors()[coordinateY + emptyFieldPosition][coordinateX] += basics.factorValues()[range];
        }
    }

    private void countVerticallyCheckedFields(int range, int coordinateY, int coordinateX) {

        userFieldCount = aiFieldCount = emptyFieldCount = emptyFieldPosition = 0;
        for (int i = 0; i < range; i++) {
            if (board[coordinateY + i][coordinateX] == Game.PLAYER_VALUE) {
                userFieldCount++;
            } else if (board[coordinateY + i][coordinateX] == Game.EMPTY_VALUE) {
                emptyFieldCount++;
                emptyFieldPosition = i;
            } else {
                aiFieldCount++;
            }
        }
    }

    private void analyzeDiagonallyLeftPossibilities(int coordinateY, int tableHeight, int coordinateX, int tableWidth) {

        analyzeDiagonallyLeftFiveFields(coordinateY, tableHeight, coordinateX, tableWidth);

        analyzeDiagonallyLeftFourFields(coordinateY, tableHeight, coordinateX, tableWidth);

        analyzeDiagonallyLeftThreeFields(coordinateY, tableHeight, coordinateX, tableWidth);

        analyzeDiagonallyLeftTwoFields(coordinateY, tableHeight, coordinateX, tableWidth);
    }

    private void analyzeDiagonallyLeftTwoFields(int coordinateY, int tableHeight, int coordinateX, int tableWidth) {

        if (coordinateY < tableHeight - 1 && coordinateX < tableWidth - 1) {
            countDiagonallyLeftCheckedFields(2, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 1) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 2, PlayerType.USER);
                }
                if (aiFieldCount == 1) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 2, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyLeftThreeFields(int coordinateY, int tableHeight, int coordinateX, int tableWidth) {

        if (coordinateY < tableHeight - 2 && coordinateX < tableWidth - 2) {
            countDiagonallyLeftCheckedFields(3, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 2) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 3, PlayerType.USER);
                }
                if (aiFieldCount == 2) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 3, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyLeftFourFields(int coordinateY, int tableHeight, int coordinateX, int tableWidth) {

        if (coordinateY < tableHeight - 3 && coordinateX < tableWidth - 3) {
            countDiagonallyLeftCheckedFields(4, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 3) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 4, PlayerType.USER);
                }
                if (aiFieldCount == 3) {
                    increaseFactorsWhenDiagonallyLeft(coordinateY, coordinateX, tableHeight, tableWidth, 4, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyLeftFiveFields(int coordinateY, int tableHeight, int coordinateX, int tableWidth) {

        if (coordinateY < tableHeight - 4 && coordinateX < tableWidth - 4) {
            countDiagonallyLeftCheckedFields(5, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 4) {
                    dangerFactors[coordinateY + emptyFieldPosition][coordinateX + emptyFieldPosition] += dangerFactorDefaults[5];
                }
                if (aiFieldCount == 4) {
                    advantageFactors[coordinateY + emptyFieldPosition][coordinateX + emptyFieldPosition] += advantageFactorDefaults[5];
                }
            }
        }
    }

    private void increaseFactorsWhenDiagonallyLeft(int coordinateY, int coordinateX, int tableHeight, int tableWidth, int range, PlayerType playerType) {

        JustCheckedValues basics = getJustCheckedValues(playerType);

        if (coordinateY - 1 < 0
                || coordinateX - 1 < 0
                || coordinateY + range >= tableHeight
                || coordinateX + range >= tableWidth
                || board[coordinateY - 1][coordinateX - 1] == basics.opponentValue()
                || board[coordinateY + range][coordinateX + range] == basics.opponentValue()) {
            basics.factors()[coordinateY + emptyFieldPosition][coordinateX + emptyFieldPosition] += basics.factorValues()[range] / basics.ratio();
        } else {
            basics.factors()[coordinateY + emptyFieldPosition][coordinateX + emptyFieldPosition] += basics.factorValues()[range];
        }
    }

    private void countDiagonallyLeftCheckedFields(int x, int coordinateY, int coordinateX) {
        userFieldCount = aiFieldCount = emptyFieldCount = emptyFieldPosition = 0;
        for (int i = 0; i < x; i++) {
            if (board[coordinateY + i][coordinateX + i] == Game.PLAYER_VALUE) {
                userFieldCount++;
            } else if (board[coordinateY + i][coordinateX + i] == Game.EMPTY_VALUE) {
                emptyFieldCount++;
                emptyFieldPosition = i;
            } else {
                aiFieldCount++;
            }
        }
    }

    private void analyzeDiagonallyRightPossibilities(int coordinateY, int coordinateX, int tableWidth, int tableHeight) {

        analyzeDiagonallyRightFiveFields(coordinateY, coordinateX, tableWidth);

        analyzeDiagonallyRightFourFields(coordinateY, coordinateX, tableWidth, tableHeight);

        analyzeDiagonallyRightThreeFields(coordinateY, coordinateX, tableWidth, tableHeight);

        analyzeDiagonallyRightTwoFields(coordinateY, coordinateX, tableWidth, tableHeight);
    }

    private void analyzeDiagonallyRightTwoFields(int coordinateY, int coordinateX, int tableWidth, int tableHeight) {

        if (coordinateY > 0 && coordinateX < tableWidth - 1) {
            countDiagonallyRightCheckedFields(2, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 1) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 2, PlayerType.USER);
                }
                if (aiFieldCount == 1) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 2, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyRightThreeFields(int coordinateY, int coordinateX, int tableWidth, int tableHeight) {

        if (coordinateY > 1 && coordinateX < tableWidth - 2) {
            countDiagonallyRightCheckedFields(3, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 2) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 3, PlayerType.USER);
                }
                if (aiFieldCount == 2) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 3, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyRightFourFields(int coordinateY, int coordinateX, int tableWidth, int tableHeight) {

        if (coordinateY > 2 && coordinateX < tableWidth - 3) {
            countDiagonallyRightCheckedFields(4, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {
                if (userFieldCount == 3) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 4, PlayerType.USER);
                }
                if (aiFieldCount == 3) {
                    increaseFactorsWhenDiagonallyRight(coordinateY, coordinateX, tableHeight, tableWidth, 4, PlayerType.AI);
                }
            }
        }
    }

    private void analyzeDiagonallyRightFiveFields(int coordinateY, int coordinateX, int tableWidth) {

        if (coordinateY > 3 && coordinateX < tableWidth - 4) {
            countDiagonallyRightCheckedFields(5, coordinateY, coordinateX);

            if (emptyFieldCount == 1) {

                if (userFieldCount == 4) {
                    dangerFactors[coordinateY - emptyFieldPosition][coordinateX + emptyFieldPosition] += dangerFactorDefaults[5];
                }

                if (aiFieldCount == 4) {
                    advantageFactors[coordinateY - emptyFieldPosition][coordinateX + emptyFieldPosition] += advantageFactorDefaults[5];
                }
            }
        }
    }

    private void increaseFactorsWhenDiagonallyRight(int coordinateY, int coordinateX, int tableHeight, int tableWidth, int range, PlayerType playerType) {

        JustCheckedValues basics = getJustCheckedValues(playerType);

        if (coordinateY + 1 >= tableHeight
                || coordinateX - 1 < 0
                || coordinateY - range < 0
                || coordinateX + range >= tableWidth
                || board[coordinateY + 1][coordinateX - 1] == basics.opponentValue()
                || board[coordinateY - range][coordinateX + range] == basics.opponentValue()) {
            basics.factors()[coordinateY - emptyFieldPosition][coordinateX + emptyFieldPosition] += basics.factorValues()[range] / basics.ratio();
        } else {
            basics.factors()[coordinateY - emptyFieldPosition][coordinateX + emptyFieldPosition] += basics.factorValues()[range];
        }
    }

    private void countDiagonallyRightCheckedFields(int range, int coordinateY, int coordinateX) {

        userFieldCount = aiFieldCount = emptyFieldCount = emptyFieldPosition = 0;
        for (int i = 0; i < range; i++) {
            if (board[coordinateY - i][coordinateX + i] == Game.PLAYER_VALUE) {
                userFieldCount++;
            } else if (board[coordinateY - i][coordinateX + i] == Game.EMPTY_VALUE) {
                emptyFieldCount++;
                emptyFieldPosition = i;
            } else {
                aiFieldCount++;
            }
        }
    }

    private StepDetails calculateRandomStep(Game gameBoard) {

        StepDetails step = new StepDetails();

        do {
            step.setCoordinateY(randomNumber(gameBoard.getBoardHeight() - 1));
            step.setCoordinateX(randomNumber(gameBoard.getBoardWidth() - 1));
        } while (!gameBoard.isCellEmpty(step.getCoordinateY(), step.getCoordinateX()));

        return step;
    }

    private int randomNumber(int maximum) {

        return new Random().nextInt(maximum + 1);
    }
}