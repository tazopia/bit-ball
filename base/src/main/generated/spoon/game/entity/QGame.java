package spoon.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QGame is a Querydsl query type for Game
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QGame extends EntityPathBase<Game> {

    private static final long serialVersionUID = 1086149758L;

    public static final QGame game = new QGame("game");

    public final NumberPath<Long> amountAway = createNumber("amountAway", Long.class);

    public final NumberPath<Long> amountDraw = createNumber("amountDraw", Long.class);

    public final NumberPath<Long> amountHome = createNumber("amountHome", Long.class);

    public final NumberPath<Long> amountTotal = createNumber("amountTotal", Long.class);

    public final BooleanPath autoUpdate = createBoolean("autoUpdate");

    public final BooleanPath betAway = createBoolean("betAway");

    public final BooleanPath betDraw = createBoolean("betDraw");

    public final BooleanPath betHome = createBoolean("betHome");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final BooleanPath deleted = createBoolean("deleted");

    public final BooleanPath enabled = createBoolean("enabled");

    public final EnumPath<spoon.game.domain.GameCode> gameCode = createEnum("gameCode", spoon.game.domain.GameCode.class);

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final StringPath groupId = createString("groupId");

    public final NumberPath<Double> handicap = createNumber("handicap", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath league = createString("league");

    public final EnumPath<spoon.game.domain.MenuCode> menuCode = createEnum("menuCode", spoon.game.domain.MenuCode.class);

    public final NumberPath<Double> oddsAway = createNumber("oddsAway", Double.class);

    public final NumberPath<Double> oddsDraw = createNumber("oddsDraw", Double.class);

    public final NumberPath<Double> oddsHome = createNumber("oddsHome", Double.class);

    public final NumberPath<Double> oddsRate = createNumber("oddsRate", Double.class);

    public final EnumPath<spoon.game.domain.GameResult> result = createEnum("result", spoon.game.domain.GameResult.class);

    public final NumberPath<Integer> scoreAway = createNumber("scoreAway", Integer.class);

    public final NumberPath<Integer> scoreHome = createNumber("scoreHome", Integer.class);

    public final StringPath siteCode = createString("siteCode");

    public final StringPath siteId = createString("siteId");

    public final NumberPath<Double> sort = createNumber("sort", Double.class);

    public final StringPath special = createString("special");

    public final StringPath sports = createString("sports");

    public final StringPath teamAway = createString("teamAway");

    public final StringPath teamHome = createString("teamHome");

    public final NumberPath<Long> udt = createNumber("udt", Long.class);

    public final NumberPath<Integer> upCount = createNumber("upCount", Integer.class);

    public final EnumPath<spoon.game.domain.UpDown> upDownAway = createEnum("upDownAway", spoon.game.domain.UpDown.class);

    public final EnumPath<spoon.game.domain.UpDown> upDownDraw = createEnum("upDownDraw", spoon.game.domain.UpDown.class);

    public final EnumPath<spoon.game.domain.UpDown> upDownHome = createEnum("upDownHome", spoon.game.domain.UpDown.class);

    public final StringPath ut = createString("ut");

    public QGame(String variable) {
        super(Game.class, forVariable(variable));
    }

    public QGame(Path<? extends Game> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGame(PathMetadata metadata) {
        super(Game.class, metadata);
    }

}

