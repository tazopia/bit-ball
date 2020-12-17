package spoon.gameZone.bitcoin5.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBitcoin5 is a Querydsl query type for Bitcoin5
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBitcoin5 extends EntityPathBase<Bitcoin5> {

    private static final long serialVersionUID = 964988190L;

    public static final QBitcoin5 bitcoin5 = new QBitcoin5("bitcoin5");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final StringPath bs = createString("bs");

    public final BooleanPath cancel = createBoolean("cancel");

    public final NumberPath<Double> close = createNumber("close", Double.class);

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Integer> hi = createNumber("hi", Integer.class);

    public final StringPath hi_oe = createString("hi_oe");

    public final StringPath hi_ou = createString("hi_ou");

    public final NumberPath<Double> high = createNumber("high", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> lo = createNumber("lo", Integer.class);

    public final StringPath lo_oe = createString("lo_oe");

    public final StringPath lo_ou = createString("lo_ou");

    public final NumberPath<Double> low = createNumber("low", Double.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final NumberPath<Double> open = createNumber("open", Double.class);

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QBitcoin5(String variable) {
        super(Bitcoin5.class, forVariable(variable));
    }

    public QBitcoin5(Path<? extends Bitcoin5> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBitcoin5(PathMetadata metadata) {
        super(Bitcoin5.class, metadata);
    }

}

