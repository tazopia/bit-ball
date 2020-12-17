package spoon.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QGameLogger is a Querydsl query type for GameLogger
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QGameLogger extends EntityPathBase<GameLogger> {

    private static final long serialVersionUID = 465330094L;

    public static final QGameLogger gameLogger = new QGameLogger("gameLogger");

    public final BooleanPath autoUpdate = createBoolean("autoUpdate");

    public final BooleanPath betAway = createBoolean("betAway");

    public final BooleanPath betDraw = createBoolean("betDraw");

    public final BooleanPath betHome = createBoolean("betHome");

    public final DateTimePath<java.util.Date> changeDate = createDateTime("changeDate", java.util.Date.class);

    public final BooleanPath closing = createBoolean("closing");

    public final BooleanPath deleted = createBoolean("deleted");

    public final BooleanPath enabled = createBoolean("enabled");

    public final EnumPath<spoon.game.domain.GameCode> gameCode = createEnum("gameCode", spoon.game.domain.GameCode.class);

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> gameId = createNumber("gameId", Long.class);

    public final NumberPath<Double> handicap = createNumber("handicap", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath league = createString("league");

    public final StringPath leagueFlag = createString("leagueFlag");

    public final EnumPath<spoon.game.domain.MenuCode> menuCode = createEnum("menuCode", spoon.game.domain.MenuCode.class);

    public final NumberPath<Double> oddsAway = createNumber("oddsAway", Double.class);

    public final NumberPath<Double> oddsDraw = createNumber("oddsDraw", Double.class);

    public final NumberPath<Double> oddsHome = createNumber("oddsHome", Double.class);

    public final NumberPath<Double> oddsRate = createNumber("oddsRate", Double.class);

    public final NumberPath<Integer> scoreAway = createNumber("scoreAway", Integer.class);

    public final NumberPath<Integer> scoreHome = createNumber("scoreHome", Integer.class);

    public final StringPath special = createString("special");

    public final StringPath sports = createString("sports");

    public final StringPath teamAway = createString("teamAway");

    public final StringPath teamHome = createString("teamHome");

    public final StringPath userid = createString("userid");

    public QGameLogger(String variable) {
        super(GameLogger.class, forVariable(variable));
    }

    public QGameLogger(Path<? extends GameLogger> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGameLogger(PathMetadata metadata) {
        super(GameLogger.class, metadata);
    }

}

