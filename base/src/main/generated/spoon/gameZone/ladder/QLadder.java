package spoon.gameZone.ladder;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLadder is a Querydsl query type for Ladder
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLadder extends EntityPathBase<Ladder> {

    private static final long serialVersionUID = -1685899301L;

    public static final QLadder ladder = new QLadder("ladder");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath line = createString("line");

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath start = createString("start");

    public QLadder(String variable) {
        super(Ladder.class, forVariable(variable));
    }

    public QLadder(Path<? extends Ladder> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLadder(PathMetadata metadata) {
        super(Ladder.class, metadata);
    }

}

