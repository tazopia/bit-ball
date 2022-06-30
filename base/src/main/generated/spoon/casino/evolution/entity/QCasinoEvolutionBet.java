package spoon.casino.evolution.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCasinoEvolutionBet is a Querydsl query type for CasinoEvolutionBet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCasinoEvolutionBet extends EntityPathBase<CasinoEvolutionBet> {

    private static final long serialVersionUID = 268302471L;

    public static final QCasinoEvolutionBet casinoEvolutionBet = new QCasinoEvolutionBet("casinoEvolutionBet");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> betMoney = createNumber("betMoney", Long.class);

    public final StringPath casinoId = createString("casinoId");

    public final StringPath gameId = createString("gameId");

    public final StringPath gameType = createString("gameType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath recommender = createString("recommender");

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> regDay = createDate("regDay", java.time.LocalDate.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath round = createString("round");

    public final StringPath status = createString("status");

    public final StringPath title = createString("title");

    public final StringPath type = createString("type");

    public final StringPath userid = createString("userid");

    public final StringPath vendor = createString("vendor");

    public final NumberPath<Long> winMoney = createNumber("winMoney", Long.class);

    public QCasinoEvolutionBet(String variable) {
        super(CasinoEvolutionBet.class, forVariable(variable));
    }

    public QCasinoEvolutionBet(Path<? extends CasinoEvolutionBet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCasinoEvolutionBet(PathMetadata metadata) {
        super(CasinoEvolutionBet.class, metadata);
    }

}

