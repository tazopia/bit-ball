package spoon.casino.evolution.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evolution.entity.CasinoEvolutionBet;
import spoon.casino.evolution.entity.QCasinoEvolutionBet;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CasinoEvolutionDao {

    private final JPAQueryFactory queryFactory;

    private final CasinoEvolutionBetRepository casinoEvolutionBetRepository;

    private static final QCasinoEvolutionBet bet = QCasinoEvolutionBet.casinoEvolutionBet;

    public long lastTransactionId() {
        return Optional.ofNullable(queryFactory.select(bet.id)
                .from(bet)
                .orderBy(bet.id.desc())
                .fetchFirst())
                .orElse(0L);
    }

    @Transactional
    public void save(CasinoEvolutionBet bet) {
        casinoEvolutionBetRepository.save(bet);
    }

    @Transactional
    public Optional<CasinoEvolutionBet> getOne(long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(bet)
                        .where(bet.id.eq(id))
                        .fetchFirst()
        );
    }
}
