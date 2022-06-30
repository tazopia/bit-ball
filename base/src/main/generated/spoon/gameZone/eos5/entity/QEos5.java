package spoon.gameZone.eos5.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QEos5 is a Querydsl query type for Eos5
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEos5 extends EntityPathBase<Eos5> {

    private static final long serialVersionUID = 274949512L;

    public static final QEos5 eos5 = new QEos5("eos5");

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

    public QEos5(String variable) {
        super(Eos5.class, forVariable(variable));
    }

    public QEos5(Path<? extends Eos5> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEos5(PathMetadata metadata) {
        super(Eos5.class, metadata);
    }

}

