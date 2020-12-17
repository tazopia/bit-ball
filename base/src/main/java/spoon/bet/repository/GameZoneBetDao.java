package spoon.bet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spoon.bet.entity.Bet;
import spoon.bet.entity.QBet;
import spoon.common.utils.DateUtils;
import spoon.game.domain.MenuCode;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class GameZoneBetDao {

    private final JPAQueryFactory queryFactory;

    private static final QBet BET = QBet.bet;

    public List<Bet> zoneBetting(String userid, MenuCode menuCode) {
        return queryFactory
                .select(BET)
                .from(BET)
                .where(
                        BET.userid.eq(userid),
                        BET.menuCode.eq(menuCode),
                        BET.deleted.isFalse(),
                        BET.betDate.after(DateUtils.beforeDays(1))
                )
                .limit(5)
                .orderBy(BET.id.desc())
                .fetch();
    }
}
