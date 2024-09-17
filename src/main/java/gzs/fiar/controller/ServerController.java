package gzs.fiar.controller;

import gzs.fiar.dto.ClickDetailsDto;
import gzs.fiar.dto.ServerStatusDto;
import gzs.fiar.service.ServerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/server")
public class ServerController {

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverStatusService) {

        this.serverService = serverStatusService;
    }

    @Secured("ROLE_TESTER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/status")
    public ServerStatusDto getStatus() {

        log.debug("Server status requested");

        return serverService.getStatus();
    }

    @Secured("ROLE_TESTER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/log")
    public String getLogFile() {

            log.debug("Server log requested");

            return serverService.getLog();
    }

    @ResponseStatus(HttpStatus.OK)
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
