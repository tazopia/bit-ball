package spoon.gameZone.KenoLadder;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QKenoLadder is a Querydsl query type for KenoLadder
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QKenoLadder extends EntityPathBase<KenoLadder> {

    private static final long serialVersionUID = 852609499L;

    public static final QKenoLadder kenoLadder = new QKenoLadder("kenoLadder");

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

    public QKenoLadder(String variable) {
        super(KenoLadder.class, forVariable(variable));
    }

    public QKenoLadder(Path<? extends KenoLadder> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKenoLadder(PathMetadata metadata) {
        super(KenoLadder.class, metadata);
    }

}

