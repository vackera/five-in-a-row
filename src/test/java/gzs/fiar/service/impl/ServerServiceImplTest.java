package gzs.fiar.service.impl;

import gzs.fiar.domain.Click;
import gzs.fiar.domain.Statistic;
import gzs.fiar.dto.ServerStatusDto;
import gzs.fiar.logic.GameManager;
import gzs.fiar.repository.ClickRepository;
import gzs.fiar.service.StatisticService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.RequiredSearch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerServiceImplTest {

    @Mock
    private StatisticService statisticService;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private GameManager gameManager;

    @Mock
    private MeterRegistry meterRegistry;

    private ServerServiceImpl underTest;

    private final String logFilePath = "test-folder/logfile.log";

    @BeforeEach
    public void setUp() throws IOException {

        underTest = new ServerServiceImpl(statisticService, clickRepository, gameManager, meterRegistry);
        ReflectionTestUtils.setField(underTest, "logFilePath", logFilePath);
        Path path = Paths.get(logFilePath).getParent();
        if (path != null && !Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Test
    public void test_getStatus() {

        //GIVEN
        Statistic statistic = new Statistic();
        statistic.setLevel(1L);
        statistic.setGameCreated(10);
        statistic.setGameStarted(8);
        statistic.setGameFinished(7);
        statistic.setPlayerWon(5);
        statistic.setAiWon(2);
        statistic.setDraw(1);

        when(statisticService.getStatisticByLevel(1)).thenReturn(statistic);

        RequiredSearch requiredSearch = mock(RequiredSearch.class);
        Gauge gauge = mock(Gauge.class);
        when(meterRegistry.get("process.uptime")).thenReturn(requiredSearch);
        when(requiredSearch.gauge()).thenReturn(gauge);
        when(gauge.value()).thenReturn(3600.0);

        when(gameManager.getActiveGamesCounter()).thenReturn(3L);

        //WHEN
        ServerStatusDto status = underTest.getStatus();

        //THEN
        assertEquals(ZonedDateTime.now().minusHours(1).getHour(), status.getServerStartTime().getHour());
        assertEquals(Duration.ofHours(1), status.getServerUptime());
        assertEquals(10, status.getGameCreated());
        assertEquals(8, status.getGameStarted());
        assertEquals(7, status.getGameFinished());
        assertEquals(5, status.getPlayerWon());
        assertEquals(2, status.getAiWon());
        assertEquals(1, status.getDraw());
        assertEquals(3, status.getActiveGames());
    }


    @Test
    public void test_logClick() {

        //GIVEN
        String link = "github";
        String userAgent = "Mozilla/5.0";
        String language = "en";
        String ipAddress = "123.234.123.234";
        String screenWidth = "1920";
        String screenHeight = "1080";

        //WHEN
        underTest.logClick(link, userAgent, language, ipAddress, screenWidth, screenHeight);

        //THEN
        Click expectedClick = new Click();
        expectedClick.setDate(now());
        expectedClick.setLink(link);
        expectedClick.setUserAgent(userAgent);
        expectedClick.setLanguage(language);
        expectedClick.setIpAddress(ipAddress);
        expectedClick.setScreenSize(screenWidth + " x " + screenHeight);

        verify(clickRepository, times(1)).save(argThat(click ->
                click.getLink().equals(expectedClick.getLink()) &&
                        click.getUserAgent().equals(expectedClick.getUserAgent()) &&
                        click.getLanguage().equals(expectedClick.getLanguage()) &&
                        click.getIpAddress().equals(expectedClick.getIpAddress()) &&
                        click.getScreenSize().equals(expectedClick.getScreenSize())));
    }

    @Test
    public void test_getLog_success() throws IOException {

        //GIVEN
        Path path = Paths.get(logFilePath);
        String expectedLogContent = "Log content";
        Files.writeString(path, expectedLogContent);

        //WHEN
        String response = underTest.getLog();

        //THEN
        assertEquals(expectedLogContent, response);
    }

    @Test
    public void test_getLog_error() throws IOException {

        mockStatic(Files.class);

        // GIVEN
        when(Files.readString(any(Path.class))).thenThrow(new IOException("File not found"));

        // WHEN
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            underTest.getLog();
        });

        // THEN
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
        assertEquals("Error reading log file", exception.getReason());
    }

    @AfterEach
    public void tearDown() throws IOException {

        Path path = Paths.get(logFilePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }

        Path parentDir = path.getParent();
        if (parentDir != null && Files.exists(parentDir) && Files.list(parentDir).findAny().isEmpty()) {
            Files.delete(parentDir);
        }
    }
}
