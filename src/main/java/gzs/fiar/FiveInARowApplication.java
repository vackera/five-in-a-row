package gzs.fiar;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import gzs.fiar.domain.Statistic;
import gzs.fiar.logic.AIPlayer;
import gzs.fiar.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class FiveInARowApplication {

    private final StatisticService statisticService;

    @Autowired
    public FiveInARowApplication(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public static void main(String[] args) {

        SpringApplication.run(FiveInARowApplication.class, args);
    }

    @PostConstruct
    private void initStatisticFields() {

        if (statisticService.getFieldCount() == 0) {
            Statistic statistic = new Statistic();
            for (long i = 1; i <= AIPlayer.AI_LEVELS; i++) {
                statistic.setLevel(i);
                statisticService.createStatisticField(statistic);
            }
            log.error("Statistic table is empty, level fields created");
        }
    }
}