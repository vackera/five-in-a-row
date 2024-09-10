package gzs.fiar.scheduling;

import lombok.extern.slf4j.Slf4j;
import gzs.fiar.logic.GameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    private final GameManager gameManager;

    @Value("${scheduled.interval}")
    private long fixedRate;

    @Autowired
    public ScheduledTasks(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Scheduled(fixedRateString = "${scheduled.interval}")
    public void performTask() {

        long deletedGames = gameManager.deleteInactiveGames();
        log.info("({}) games deleted with inactive ID", deletedGames);
    }

}
