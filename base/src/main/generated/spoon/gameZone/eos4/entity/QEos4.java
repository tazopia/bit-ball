package spoon.gameZone.eos4.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QEos4 is a Querydsl query type for Eos4
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEos4 extends EntityPathBase<Eos4> {

    private static final long serialVersionUID = 568352518L;

    public static final QEos4 eos4 = new QEos4("eos4");

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

    public QEos4(String variable) {
        super(Eos4.class, forVariable(variable));
    }

    public QEos4(Path<? extends Eos4> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEos4(PathMetadata metadata) {
        super(Eos4.class, metadata);
    }

}

