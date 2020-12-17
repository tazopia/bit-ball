package spoon.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLeague is a Querydsl query type for League
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLeague extends EntityPathBase<League> {

    private static final long serialVersionUID = 259352507L;

    public static final QLeague league = new QLeague("league");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath leagueFlag = createString("leagueFlag");

    public final StringPath leagueKor = createString("leagueKor");

    public final StringPath leagueName = createString("leagueName");

    public final StringPath sports = createString("sports");

    public QLeague(String variable) {
        super(League.class, forVariable(variable));
    }

    public QLeague(Path<? extends League> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLeague(PathMetadata metadata) {
        super(League.class, metadata);
    }

}

