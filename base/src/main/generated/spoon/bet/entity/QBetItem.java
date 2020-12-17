package spoon.bet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBetItem is a Querydsl query type for BetItem
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBetItem extends EntityPathBase<BetItem> {

    private static final long serialVersionUID = 1286127891L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBetItem betItem = new QBetItem("betItem");

    public final QBet bet;

    public final NumberPath<Long> betMoney = createNumber("betMoney", Long.class);

    public final StringPath betTeam = createString("betTeam");

    public final NumberPath<Integer> betZone = createNumber("betZone", Integer.class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final EnumPath<spoon.game.domain.GameCode> gameCode = createEnum("gameCode", spoon.game.domain.GameCode.class);

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> gameId = createNumber("gameId", Long.class);

    public final EnumPath<spoon.game.domain.GameResult> gameResult = createEnum("gameResult", spoon.game.domain.GameResult.class);

    public final StringPath groupId = createString("groupId");

    public final NumberPath<Double> handicap = createNumber("handicap", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath league = createString("league");

    public final EnumPath<spoon.game.domain.MenuCode> menuCode = createEnum("menuCode", spoon.game.domain.MenuCode.class);

    public final NumberPath<Double> odds = createNumber("odds", Double.class);

    public final NumberPath<Double> oddsAway = createNumber("oddsAway", Double.class);

    public final NumberPath<Double> oddsDraw = createNumber("oddsDraw", Double.class);

    public final NumberPath<Double> oddsHome = createNumber("oddsHome", Double.class);

    public final StringPath result = createString("result");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final NumberPath<Integer> scoreAway = createNumber("scoreAway", Integer.class);

    public final NumberPath<Integer> scoreHome = createNumber("scoreHome", Integer.class);

    public final StringPath special = createString("special");

    public final StringPath sports = createString("sports");

    public final StringPath teamAway = createString("teamAway");

    public final StringPath teamHome = createString("teamHome");

    public final StringPath userid = createString("userid");

    public QBetItem(String variable) {
        this(BetItem.class, forVariable(variable), INITS);
    }

    public QBetItem(Path<? extends BetItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBetItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBetItem(PathMetadata metadata, PathInits inits) {
        this(BetItem.class, metadata, inits);
    }

    public QBetItem(Class<? extends BetItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bet = inits.isInitialized("bet") ? new QBet(forProperty("bet")) : null;
    }

}

