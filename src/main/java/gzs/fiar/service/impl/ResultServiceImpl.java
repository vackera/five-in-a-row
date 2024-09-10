package gzs.fiar.service.impl;

import lombok.extern.slf4j.Slf4j;
import gzs.fiar.domain.Result;
import gzs.fiar.dto.ResultDto;
import gzs.fiar.repository.ResultRepository;
import gzs.fiar.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;

    @Autowired
    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public long saveNewResult(int level, String name, int steps, long time, float score) {

        Result result = new Result();
        result.setLevel(level);
        result.setDatePlayed(LocalDateTime.now());
        result.setPlayerName(name);
        result.setWinnerStepCount(steps);
        result.setWinnerTime(time);
        result.setScore(score);

        long resultId = resultRepository.save(result).getId();

        log.info("Player's ({}) result saved to database ({})", name, resultId);

        return resultId;
    }

    @Override
    public List<ResultDto> getBestResults() {

        Pageable pageable = PageRequest.of(0, 20);
        return resultRepository.getBestResults(1, pageable);
    }
}
