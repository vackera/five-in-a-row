package gzs.fiar.controller;

import lombok.extern.slf4j.Slf4j;
import gzs.fiar.dto.ResultDto;
import gzs.fiar.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/result")
public class ResultController {

    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {

        this.resultService = resultService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ResultDto> getBestResults() {

        List <ResultDto> response = resultService.getBestResults();
        log.debug("Best {} results listed", response.size());
        return response;
    }
}
