package spoon.gameZone.powerLadder;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPowerLadder is a Querydsl query type for PowerLadder
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPowerLadder extends EntityPathBase<PowerLadder> {

    private static final long serialVersionUID = 581993357L;

    public static final QPowerLadder powerLadder = new QPowerLadder("powerLadder");

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

    public final NumberPath<Integer> times = createNumber("times", Integer.class);

    public QPowerLadder(String variable) {
        super(PowerLadder.class, forVariable(variable));
    }

    public QPowerLadder(Path<? extends PowerLadder> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPowerLadder(PathMetadata metadata) {
        super(PowerLadder.class, metadata);
    }

}

