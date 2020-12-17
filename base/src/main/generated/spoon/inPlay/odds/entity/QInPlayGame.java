package spoon.inPlay.odds.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayGame is a Querydsl query type for InPlayGame
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayGame extends EntityPathBase<InPlayGame> {

    private static final long serialVersionUID = 599096260L;

    public static final QInPlayGame inPlayGame = new QInPlayGame("inPlayGame");

    public final StringPath aname = createString("aname");

    public final NumberPath<Long> fixtureId = createNumber("fixtureId", Long.class);

    public final StringPath hname = createString("hname");

    public final StringPath league = createString("league");

    public final StringPath location = createString("location");

    public final DateTimePath<java.util.Date> sdate = createDateTime("sdate", java.util.Date.class);

    public final StringPath sname = createString("sname");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final NumberPath<Long> ut = createNumber("ut", Long.class);

    public QInPlayGame(String variable) {
        super(InPlayGame.class, forVariable(variable));
    }

    public QInPlayGame(Path<? extends InPlayGame> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayGame(PathMetadata metadata) {
        super(InPlayGame.class, metadata);
    }

}

