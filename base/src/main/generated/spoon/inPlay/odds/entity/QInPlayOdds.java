package spoon.inPlay.odds.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayOdds is a Querydsl query type for InPlayOdds
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayOdds extends EntityPathBase<InPlayOdds> {

    private static final long serialVersionUID = 599337206L;

    public static final QInPlayOdds inPlayOdds = new QInPlayOdds("inPlayOdds");

    public final StringPath baseLine = createString("baseLine");

    public final NumberPath<Long> fixtureId = createNumber("fixtureId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> lastUpdate = createNumber("lastUpdate", Long.class);

    public final StringPath line = createString("line");

    public final NumberPath<Long> marketId = createNumber("marketId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath oname = createString("oname");

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final StringPath provider = createString("provider");

    public final NumberPath<Integer> settlement = createNumber("settlement", Integer.class);

    public final NumberPath<Double> startPrice = createNumber("startPrice", Double.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public QInPlayOdds(String variable) {
        super(InPlayOdds.class, forVariable(variable));
    }

    public QInPlayOdds(Path<? extends InPlayOdds> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayOdds(PathMetadata metadata) {
        super(InPlayOdds.class, metadata);
    }

}

