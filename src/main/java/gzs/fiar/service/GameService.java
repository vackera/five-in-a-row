package gzs.fiar.service;

import gzs.fiar.dto.ResponseStep;
import gzs.fiar.dto.StepDetails;

public interface GameService {

    String newGame(String previousGameID, String newGameID);

    void changePlayerName(String gameID, String name);

    ResponseStep playerDoStep(String gameID, StepDetails step);

    String getAIVersion();

}
