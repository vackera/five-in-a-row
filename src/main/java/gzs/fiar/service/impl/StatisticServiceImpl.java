package gzs.fiar.service.impl;

import gzs.fiar.domain.Statistic;
import gzs.fiar.repository.StatisticRepository;
import gzs.fiar.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Autowired
    public StatisticServiceImpl(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    @Override
    public long getFieldCount() {

        return statisticRepository.count();
    }

    @Override
    public void createStatisticField(Statistic statistic) {

        statisticRepository.save(statistic);
    }

    @Override
    public void increaseGameCreated(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setGameCreated(statistic.getGameCreated() + 1);
        statisticRepository.save(statistic);
    }

    @Override
    public void increaseGameStarted(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setGameStarted(statistic.getGameStarted() + 1);
        statisticRepository.save(statistic);
    }

    @Override
    public void increaseGameFinished(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setGameFinished(statistic.getGameFinished() + 1);
        statisticRepository.save(statistic);
    }

    @Override
    public void increasePlayerWins(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setPlayerWon(statistic.getPlayerWon() + 1);
        statisticRepository.save(statistic);
    }

    @Override
    public void increaseAIWins(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setAiWon(statistic.getAiWon() + 1);
        statisticRepository.save(statistic);
    }

    @Override
    public void increaseDraws(long level) {

        Statistic statistic = getStatisticByLevel(level);
        if (statistic == null) return;

        statistic.setDraw(statistic.getDraw() + 1);
        statisticRepository.save(statistic);
    }

    public Statistic getStatisticByLevel(long level) {

        Optional<Statistic> statistic = statisticRepository.findById(level);
        if (statistic.isEmpty()) {
            log.warn("Can't log statistics for level {}", level);
            return null;
        }

        return statistic.get();
    }
}