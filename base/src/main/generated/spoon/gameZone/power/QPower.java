package spoon.gameZone.power;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPower is a Querydsl query type for Power
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPower extends EntityPathBase<Power> {

    private static final long serialVersionUID = 1966298697L;

    public static final QPower power = new QPower("power");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final StringPath ball = createString("ball");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath overunder = createString("overunder");

    public final StringPath pb = createString("pb");

    public final StringPath pb_oddeven = createString("pb_oddeven");

    public final StringPath pb_overunder = createString("pb_overunder");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath size = createString("size");

    public final NumberPath<Integer> sum = createNumber("sum", Integer.class);

    public final NumberPath<Integer> times = createNumber("times", Integer.class);

    public QPower(String variable) {
        super(Power.class, forVariable(variable));
    }

    public QPower(Path<? extends Power> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPower(PathMetadata metadata) {
        super(Power.class, metadata);
    }

}

