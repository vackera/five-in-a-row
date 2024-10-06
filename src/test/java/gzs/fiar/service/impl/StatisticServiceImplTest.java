package gzs.fiar.service.impl;

import gzs.fiar.domain.Statistic;
import gzs.fiar.repository.StatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceImplTest {

    @Mock
    private StatisticRepository statisticRepository;

    private StatisticServiceImpl underTest;

    @BeforeEach
    public void setUp() {

        underTest = new StatisticServiceImpl(statisticRepository);
    }

    @Test
    public void test_increaseGameCreated() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setGameCreated(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increaseGameCreated(level);

        //THEN
        assertEquals(1, statistic.getGameCreated());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_increaseGameCreated_statisticNotFound() {

        //GIVEN
        long level = 1L;
        when(statisticRepository.findById(level)).thenReturn(Optional.empty());

        //WHEN
        underTest.increaseGameCreated(level);

        //THEN
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    public void test_increaseGameStarted() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setGameStarted(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increaseGameStarted(level);

        //THEN
        assertEquals(1, statistic.getGameStarted());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_increaseGameFinished() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setGameFinished(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increaseGameFinished(level);

        //THEN
        assertEquals(1, statistic.getGameFinished());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_increasePlayerWins() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setPlayerWon(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increasePlayerWins(level);

        //THEN
        assertEquals(1, statistic.getPlayerWon());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_increaseAIWins() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setAiWon(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increaseAIWins(level);

        //THEN
        assertEquals(1, statistic.getAiWon());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_increaseDraws() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        statistic.setDraw(0);
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        underTest.increaseDraws(level);

        //THEN
        assertEquals(1, statistic.getDraw());
        verify(statisticRepository, times(1)).save(statistic);
    }

    @Test
    public void test_getFieldCount() {

        //GIVEN
        when(statisticRepository.count()).thenReturn(5L);

        //WHEN
        long count = underTest.getFieldCount();

        //THEN
        assertEquals(5L, count);
        verify(statisticRepository, times(1)).count();
    }

    @Test
    public void test_getStatisticByLevel_statisticFound() {

        //GIVEN
        long level = 1L;
        Statistic statistic = new Statistic();
        when(statisticRepository.findById(level)).thenReturn(Optional.of(statistic));

        //WHEN
        Statistic result = underTest.getStatisticByLevel(level);

        //THEN
        assertEquals(statistic, result);
    }

    @Test
    public void test_getStatisticByLevel_statisticNotFound() {

        //GIVEN
        long level = 1L;
        when(statisticRepository.findById(level)).thenReturn(Optional.empty());

        //WHEN
        Statistic result = underTest.getStatisticByLevel(level);

        //THEN
        assertNull(result);
    }
}
