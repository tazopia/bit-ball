package spoon.inPlay.bet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayBet is a Querydsl query type for InPlayBet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayBet extends EntityPathBase<InPlayBet> {

    private static final long serialVersionUID = 2068721314L;

    public static final QInPlayBet inPlayBet = new QInPlayBet("inPlayBet");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final DateTimePath<java.util.Date> betDate = createDateTime("betDate", java.util.Date.class);

    public final NumberPath<Long> betMoney = createNumber("betMoney", Long.class);

    public final NumberPath<Double> betOdds = createNumber("betOdds", Double.class);

    public final BooleanPath black = createBoolean("black");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final StringPath corner = createString("corner");

    public final BooleanPath deleted = createBoolean("deleted");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> expMoney = createNumber("expMoney", Long.class);

    public final NumberPath<Long> fixtureId = createNumber("fixtureId", Long.class);

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> hitMoney = createNumber("hitMoney", Long.class);

    public final NumberPath<Double> hitOdds = createNumber("hitOdds", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath league = createString("league");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath line = createString("line");

    public final NumberPath<Long> marketId = createNumber("marketId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> oddsId = createNumber("oddsId", Long.class);

    public final StringPath oname = createString("oname");

    public final BooleanPath payment = createBoolean("payment");

    public final StringPath recommender = createString("recommender");

    public final NumberPath<Integer> result = createNumber("result", Integer.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath score = createString("score");

    public final StringPath sports = createString("sports");

    public final StringPath status = createString("status");

    public final StringPath teamAway = createString("teamAway");

    public final StringPath teamHome = createString("teamHome");

    public final StringPath userid = createString("userid");

    public QInPlayBet(String variable) {
        super(InPlayBet.class, forVariable(variable));
    }

    public QInPlayBet(Path<? extends InPlayBet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayBet(PathMetadata metadata) {
        super(InPlayBet.class, metadata);
    }

}

