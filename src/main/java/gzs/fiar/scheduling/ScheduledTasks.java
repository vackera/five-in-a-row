package gzs.fiar.scheduling;

import gzs.fiar.logic.GameManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    private final GameManager gameManager;

    @Autowired
    public ScheduledTasks(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Scheduled(fixedRateString = "${scheduled.interval}")
    public void performTask() {

        long deletedGames = gameManager.deleteInactiveGames();
        log.info("({}) inactive games deleted", deletedGames);
    }

}
