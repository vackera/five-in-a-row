package gzs.fiar.repository;

import gzs.fiar.domain.Result;
import gzs.fiar.dto.ResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("select new gzs.fiar.dto.ResultDto(r. id, r.datePlayed, r.playerName, r.winnerStepCount, r.winnerTime, r.score) from Result r where r.level = ?1 order by r.score asc, r.winnerStepCount asc, r.winnerTime asc, r.datePlayed asc")
    List<ResultDto> getBestResults(int level, Pageable pageable);
}
