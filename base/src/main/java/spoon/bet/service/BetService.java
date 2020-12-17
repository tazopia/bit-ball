package spoon.bet.service;

import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface BetService {

    /**
     * 스포츠 베팅
     */
    boolean addGameBetting(Bet bet);

    /**
     * 스포츠 베팅 베팅금액 업데이트
     */
    long updateGameAmount(long... gameIds);

    /**
     * 게임존(실시간) 베팅
     */
    boolean addZoneBetting(Bet bet);

    /**
     * 베팅취소 bet
     */
    AjaxResult cancelUser(Long betId);

    /**
     * 베팅취소 bet
     */
    AjaxResult cancelBet(Long betId);

    /**
     * 베팅취소 item
     */
    AjaxResult cancelItem(Long betId, Long itemId);

    /**
     * 회원별 게임별 베팅 횟수, 베팅머니, 적중금을 가져온다.
     */
    List<BetDto.UserBet> userBetList(String userid);


    /**
     * 사용자가 자신의 베팅을 보이지 않게 delete 를 true 로 바꾼다.
     */
    AjaxResult delete(BetDto.Delete command);

    /**
     * 종료된 베팅을 보이지 않게 delete 를 true 로 바꾼다. 종료내역 전체 삭제
     */
    AjaxResult deleteClosing(String userid);

    /**
     * 베팅 알람을 취소한다.
     */
    AjaxResult removeBlack(Long betId);

    /**
     * 현재 진행중인 베팅을 검색한다.
     */
    Iterable<Bet> getCurrentBet(String userid);

    /**
     * 해당 베팅아이템을 가져온다.
     */
    Iterable<BetItem> betMoneyByItem(String userid, Long gameId, String betTeam);
}
