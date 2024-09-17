package gzs.fiar.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import gzs.fiar.domain.Click;
import gzs.fiar.domain.Statistic;
import gzs.fiar.dto.ServerStatusDto;
import gzs.fiar.logic.GameManager;
import gzs.fiar.repository.ClickRepository;
import gzs.fiar.service.ServerService;
import gzs.fiar.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class ServerServiceImpl implements ServerService {

    @Value("${logging.file.name}")
    private String logFilePath;

    private final StatisticService statisticService;
    private final ClickRepository clickRepository;
    private final GameManager gameManager;
    private final MeterRegistry meterRegistry;

    @Autowired
    public ServerServiceImpl(StatisticService statisticService, ClickRepository clickRepository, GameManager gameManager, MeterRegistry meterRegistry) {

        this.statisticService = statisticService;
        this.clickRepository = clickRepository;
        this.gameManager = gameManager;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public ServerStatusDto getStatus() {

        ServerStatusDto status = new ServerStatusDto();

        double uptimeInSeconds = meterRegistry.get("process.uptime").gauge().value();
        Duration uptimeDuration = Duration.ofSeconds((long) uptimeInSeconds);

        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime serverStartTime = currentTime.minus(uptimeDuration);

        status.setServerStartTime(serverStartTime);
        status.setServerUptime(uptimeDuration);

        Statistic statistic = statisticService.getStatisticByLevel(1);

        status.setGameCreated(statistic.getGameCreated());
        status.setGameStarted(statistic.getGameStarted());
        status.setGameFinished(statistic.getGameFinished());
        status.setPlayerWon(statistic.getPlayerWon());
        status.setAiWon(statistic.getAiWon());
        status.setDraw(statistic.getDraw());

        status.setActiveGames(gameManager.getActiveGamesCounter());

        return status;
    }

    @Override
    public void logClick(String link, String userAgent, String language, String ipAddress, String screenWidth, String screenHeight) {

        Click click = new Click();
        click.setDate(LocalDateTime.now());
        click.setLink(link);
        click.setUserAgent(userAgent);
        click.setLanguage(language);
        click.setIpAddress(ipAddress);
        click.setScreenSize(screenWidth + " x " + screenHeight);
        clickRepository.save(click);
    }

    @Override
    public String getLog() {

        String response;
        try {
            Path path = Paths.get(logFilePath);
            response = Files.readString(path);

            return response;
        } catch (IOException e) {
            log.error("Error reading log file: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error reading log file", e);
        }
    }
}
