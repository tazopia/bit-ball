package spoon.bet.service;


import spoon.bet.domain.BetDto;
import spoon.bet.entity.BetItem;
import spoon.game.domain.MenuCode;
import spoon.support.web.AjaxResult;

import java.util.Date;

public interface BetGameService {

    /**
     * 파라메타를 받아서 스포츠 베팅을 셋팅한다.
     */
    AjaxResult addGameBetting(BetDto.BetGame betGame);

    /**
     * 베팅한 경기의 시간이 변경 되었다.
     */
    void updateGameDate(Long id, Date gameDate);

    /**
     * menuCode 와 gameDate 로 발란스 대상 베팅을 가져온다.
     */
    Iterable<BetItem> getBalanceBet(MenuCode menuCode, String sdate);

}
