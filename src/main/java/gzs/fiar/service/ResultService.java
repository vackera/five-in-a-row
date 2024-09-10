package gzs.fiar.service;

import gzs.fiar.dto.ResultDto;

import java.util.List;

public interface ResultService {

    long saveNewResult(int level, String name, int steps, long time, float score);

    List<ResultDto> getBestResults();
}
