package spoon.bet.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.game.domain.MenuCode;

import java.util.List;

public interface BetListService {

    Page<Bet> adminPage(BetDto.Command command, Pageable pageable);

    Page<Bet> userPage(BetDto.UserCommand command, Pageable pageable);

    Page<Bet> sellerPage(BetDto.SellerCommand command, Pageable pageable);

    List<Bet> listByGame(Long gameId, String betTeam);

    Iterable<BetItem> listByGame(MenuCode menuCode, String sdate);

    Iterable<Bet> listByBoard(Long[] betIds);

    Iterable<Bet> listByBoard(String betId, String userid);

}
