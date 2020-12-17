package spoon.inPlay.odds.service;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayMarket;
import spoon.inPlay.config.entity.QInPlayMarket;
import spoon.inPlay.odds.domain.InPlayDto;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.odds.entity.InPlayOddsDone;
import spoon.inPlay.odds.entity.QInPlayOdds;
import spoon.inPlay.odds.entity.QInPlayOddsDone;
import spoon.inPlay.odds.repository.InPlayOddsDoneRepository;
import spoon.inPlay.odds.repository.InPlayOddsRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayOddsService {

    private final InPlayOddsRepository inPlayOddsRepository;

    private final InPlayOddsDoneRepository inPlayOddsDoneRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final JPAQueryFactory queryFactory;

    private static final QInPlayOdds ODDS = QInPlayOdds.inPlayOdds;

    private static final QInPlayOddsDone DONE = QInPlayOddsDone.inPlayOddsDone;

    private static final QInPlayMarket MARKET = QInPlayMarket.inPlayMarket;

    // 사용자
    public Iterable<InPlayOdds> list(InPlayDto.OddsParam param) { // 베팅 가능한것만 가져온다.
        return queryFactory.select(ODDS).from(ODDS).where(
                ODDS.fixtureId.eq(param.getId()),
                ODDS.provider.eq(param.getPv()),
                ODDS.status.eq(1),
                ODDS.marketId.in(JPAExpressions.select(MARKET.id).from(MARKET).where(MARKET.enabled.isTrue()))
        ).orderBy(ODDS.oname.asc(), ODDS.baseLine.asc(), ODDS.name.asc()).fetch();
    }

    // 관리자
    public Iterable<InPlayOdds> adminList(InPlayDto.OddsParam param) {
        return inPlayOddsRepository.findAll(
                ODDS.fixtureId.eq(param.getId())
                        .and(ODDS.provider.eq(param.getPv()))
                        .and(ODDS.status.eq(1)),
                new Sort("oname", "baseLine", "name")
        );
    }

    public long count(InPlayDto.OddsParam param) {
        return queryFactory.select(ODDS.id).from(ODDS).where(
                ODDS.fixtureId.eq(param.getId()),
                ODDS.provider.eq(param.getPv()),
                ODDS.status.eq(1),
                ODDS.marketId.in(JPAExpressions.select(MARKET.id).from(MARKET).where(MARKET.enabled.isTrue()))
        ).fetchCount();
    }

    /**
     * 트렌젝션을 걸지 마세요.
     * 데이터 접합성을 따지지 않고 나중 데이터 엎어 씌우면 됩니다.
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void save(InPlayOdds odds) {
        if (!InPlayConfig.getMarket().containsKey(odds.getMarketId())) {
            eventPublisher.publishEvent(InPlayMarket.of(odds));
        }
        try {
            inPlayOddsRepository.save(odds);
        } catch (RuntimeException e) {
            log.error("배당을 저장하지 못하였습니다.", e);
        }
    }

    @Transactional
    public void updateOdds() {
        inPlayOddsRepository.findAll(ODDS.settlement.ne(0)).forEach(odds -> {
            InPlayOddsDone done = InPlayOddsDone.of(odds);
            inPlayOddsDoneRepository.save(done);
            inPlayOddsRepository.delete(odds);
        });

        // 중복 데이터 삭제
        List<InPlayOddsDone> dones = queryFactory.select(DONE)
                .from(DONE).where(DONE.id.in(
                        JPAExpressions.select(ODDS.id).from(ODDS)
                )).fetch();
        dones.forEach(x -> inPlayOddsRepository.delete(x.getId()));
    }

    @Transactional
    public void cancelOdds() {
        // 5시간 전
        long lastUpdate = System.currentTimeMillis() - (5 * 60 * 60 * 1000);
        // 여기에서 배당을 -1로 하면 updateOdds에서 취소로 베팅을 찾게 된다.
        inPlayOddsRepository.find5HourBefore(lastUpdate).forEach(x -> x.updateSettlement(-1));
    }

    public InPlayOdds getOne(long id, long fixtureId) {
        return inPlayOddsRepository.findOne(ODDS.id.eq(id).and(ODDS.fixtureId.eq(fixtureId)));
    }
}
