package gzs.fiar.service.impl;

import gzs.fiar.domain.Result;
import gzs.fiar.dto.ResultDto;
import gzs.fiar.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultServiceImplTest {

    @Mock
    private ResultRepository resultRepository;

    private ResultServiceImpl underTest;

    @BeforeEach
    public void setUp() {

        underTest = new ResultServiceImpl(resultRepository);
    }

    @Test
    public void test_saveNewResult() {

        //GIVEN
        Result result = new Result();
        result.setId(3L);
        when(resultRepository.save(any(Result.class))).thenReturn(result);

        //WHEN
        long resultId = underTest.saveNewResult(1, "Test Player", 10, 13800L, 11.38F);

        //THEN
        assertEquals(3L, resultId);
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    public void test_getBestResults_shouldReturnEmptyListWhenTableIsEmpty() {

        //GIVEN
        List<ResultDto> resultList = new ArrayList<>();
        when(resultRepository.getBestResults(anyInt(), any(Pageable.class))).thenReturn(resultList);

        //WHEN
        List<ResultDto> bestResults = underTest.getBestResults();

        //THEN
        assertNotNull(bestResults);
        assertEquals(0, bestResults.size());
        verify(resultRepository, times(1)).getBestResults(anyInt(), any(Pageable.class));
    }

    @Test
    public void test_getBestResults_shouldReturnAllResultsWhenLessThan20ResultsInTable() {

        //GIVEN
        List<ResultDto> allResults = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            allResults.add(new ResultDto(i, LocalDateTime.now(), "Test Player", 10, 10000L, 10F));
        }

        when(resultRepository.getBestResults(anyInt(), any(Pageable.class)))
                .thenAnswer(invocation -> allResults.subList(0, 8));

        //WHEN
        List<ResultDto> bestResults = underTest.getBestResults();

        //THEN
        assertNotNull(bestResults);
        assertEquals(8, bestResults.size());
        verify(resultRepository, times(1)).getBestResults(anyInt(), any(Pageable.class));
    }

    @Test
    public void test_getBestResults_shouldReturn20ResultsWhenMoreThan20ResultsInTable() {

        //GIVEN
        List<ResultDto> allResults = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            allResults.add(new ResultDto(i, LocalDateTime.now(), "Test Player", 10, 10000L, 10F));
        }

        when(resultRepository.getBestResults(anyInt(), any(Pageable.class)))
                .thenAnswer(invocation -> allResults.subList(0, 20));

        //WHEN
        List<ResultDto> bestResults = underTest.getBestResults();

        //THEN
        assertNotNull(bestResults);
        assertEquals(20, bestResults.size());
        verify(resultRepository, times(1)).getBestResults(anyInt(), any(Pageable.class));
    }
}
