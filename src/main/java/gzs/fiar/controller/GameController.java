package gzs.fiar.controller;

import gzs.fiar.logic.GameStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import gzs.fiar.dto.PlayerNameDto;
import gzs.fiar.dto.ResponseStep;
import gzs.fiar.dto.StepDetails;
import gzs.fiar.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController {

    public static final String SESSION_GAME_ATTRIBUTE = "game-id";

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new")
    public void newGame(HttpServletRequest request) {

        HttpSession oldSession = request.getSession(false);
        String previousGameID;

        if (oldSession != null) {
            previousGameID = getGameID(oldSession);
            oldSession.invalidate();
        } else {
            previousGameID = "";
        }

        HttpSession newSession = request.getSession(true);
        String newGameID = gameService.newGame(previousGameID, newSession.getId());
        newSession.setAttribute(SESSION_GAME_ATTRIBUTE, newGameID);

        log.info("({}) game created", newGameID);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/player-step")
    public ResponseStep playerDoStep(@RequestBody StepDetails step, HttpSession session) {

        String gameID = getGameID(session);
        ResponseStep response = gameService.playerDoStep(gameID, step);

        if (response.getGameStatus() != GameStatus.IN_PROGRESS) {
            log.info("({}) ended ({} steps - {}s - {})"
                    , gameID
                    , response.getStepCount()
                    , String.format("%.2f", response.getGameTimeInMillis() / 1000.0)
                    , response.getGameStatus());
        }

        return response;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/player-name")
    public void changePlayerName(HttpSession session, HttpServletResponse response, @Valid @RequestBody PlayerNameDto newName) {

        String gameID = getGameID(session);
        gameService.changePlayerName(gameID, newName.getName());

        String encodedName = newName.getName()
                .replaceAll("\\s", "_");

        Cookie cookie = new Cookie("player-name", encodedName);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        log.info("({}) player name changed ({})", gameID, newName.getName());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ai-version")
    public String getAIVersion() {

        return gameService.getAIVersion();
    }

    private String getGameID(HttpSession session) {

        return session.getAttribute(SESSION_GAME_ATTRIBUTE) == null ? "" : (String) session.getAttribute(SESSION_GAME_ATTRIBUTE);
    }

}
