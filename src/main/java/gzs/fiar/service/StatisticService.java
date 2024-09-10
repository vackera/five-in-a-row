package gzs.fiar.service;

import gzs.fiar.domain.Statistic;

public interface StatisticService {

    long getFieldCount();

    void createStatisticField(Statistic statistic);

    void increaseGameCreated(long level);

    void increaseGameStarted(long level);

    void increaseGameFinished(long level);

    void increasePlayerWins(long level);

    void increaseAIWins(long level);

    void increaseDraws(long level);

    Statistic getStatisticByLevel(long level);
}
