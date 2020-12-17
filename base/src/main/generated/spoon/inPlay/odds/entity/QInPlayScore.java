package spoon.inPlay.odds.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayScore is a Querydsl query type for InPlayScore
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayScore extends EntityPathBase<InPlayScore> {

    private static final long serialVersionUID = 1403259136L;

    public static final QInPlayScore inPlayScore = new QInPlayScore("inPlayScore");

    public final NumberPath<Long> fixtureId = createNumber("fixtureId", Long.class);

    public final StringPath liveScore = createString("liveScore");

    public QInPlayScore(String variable) {
        super(InPlayScore.class, forVariable(variable));
    }

    public QInPlayScore(Path<? extends InPlayScore> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayScore(PathMetadata metadata) {
        super(InPlayScore.class, metadata);
    }

}

