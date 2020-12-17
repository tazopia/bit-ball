package spoon.gameZone.bitcoin3.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBitcoin3 is a Querydsl query type for Bitcoin3
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBitcoin3 extends EntityPathBase<Bitcoin3> {

    private static final long serialVersionUID = -1741631206L;

    public static final QBitcoin3 bitcoin3 = new QBitcoin3("bitcoin3");

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

    public QBitcoin3(String variable) {
        super(Bitcoin3.class, forVariable(variable));
    }

    public QBitcoin3(Path<? extends Bitcoin3> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBitcoin3(PathMetadata metadata) {
        super(Bitcoin3.class, metadata);
    }

}

