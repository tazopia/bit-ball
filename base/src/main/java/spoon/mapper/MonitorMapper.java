package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.monitor.domain.MonitorDto;

import java.util.List;

public interface MonitorMapper {

    MonitorDto.Amount getAmount();

    MonitorDto.Bank getBank(@Param("start") String start, @Param("end") String end);

    List<MonitorDto.Bet> getBet();

    List<MonitorDto.Bet> getBetEnd(@Param("start") String start, @Param("end") String end);

    Long getInplay();

    Long getInplayEnd(@Param("start") String start, @Param("end") String end);

    long getSun(@Param("start") String start, @Param("end") String end);

    long getSunEnd(@Param("start") String start, @Param("end") String end);

    long getCasino(@Param("start") String start, @Param("end") String end);

    long getCasinoEnd(@Param("start") String start, @Param("end") String end);
}
