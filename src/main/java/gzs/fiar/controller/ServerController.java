package gzs.fiar.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import gzs.fiar.dto.ClickDetailsDto;
import gzs.fiar.dto.ServerStatusDto;
import gzs.fiar.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/server")
public class ServerController {

    @Value("${logging.file.name}")
    private String logFilePath;

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverStatusService) {

        this.serverService = serverStatusService;
    }

    @GetMapping("/status")
    public ServerStatusDto getStatus() {

        ServerStatusDto status = serverService.getStatus();
        log.info("Server status requested");

        return status;
    }

    @GetMapping("/log")
    public ResponseEntity<String> getLogFile() {
        try {
            Path path = Paths.get(logFilePath);
            String content = Files.readString(path);

            log.info("Server log requested");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                    .body(content);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading log file: " + e.getMessage());
        }
    }

    @PostMapping("/log-click")
    public void logClick(HttpServletRequest request, @RequestBody ClickDetailsDto clickDetails) {

        serverService.logClick(clickDetails.getLink(),
                request.getHeader("User-Agent"),
                request.getHeader("Accept-Language"),
                request.getRemoteAddr(),
                clickDetails.getScreenWidth(),
                clickDetails.getScreenHeight());
        log.info("Clicked: {}", clickDetails.getLink());
    }
}
