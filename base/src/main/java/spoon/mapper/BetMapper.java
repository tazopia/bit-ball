package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.bet.domain.BetDto;
import spoon.bet.domain.BetUserRate;
import spoon.game.domain.MenuCode;

import java.util.List;

public interface BetMapper {

    List<BetDto.ZoneAmount> zoneAmount(@Param("menuCode") MenuCode menuCode, @Param("sdate") String sdate);

    long updateGameAmount(@Param("gameIds") long[] gameIds);

    List<BetDto.UserBet> userBetList(String userid);

    List<BetUserRate> userRateList(BetDto.BetRate command);

    void deleteBets(BetDto.Delete command);

    void deleteClosingBets(String userid);
}
