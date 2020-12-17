package spoon.gameZone.lowhi;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLowhi is a Querydsl query type for Lowhi
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLowhi extends EntityPathBase<Lowhi> {

    private static final long serialVersionUID = 2096340713L;

    public static final QLowhi lowhi1 = new QLowhi("lowhi1");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lowhi = createString("lowhi");

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QLowhi(String variable) {
        super(Lowhi.class, forVariable(variable));
    }

    public QLowhi(Path<? extends Lowhi> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLowhi(PathMetadata metadata) {
        super(Lowhi.class, metadata);
    }

}

