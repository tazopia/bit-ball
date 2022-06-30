package spoon.gameZone.eos2.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QEos2 is a Querydsl query type for Eos2
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEos2 extends EntityPathBase<Eos2> {

    private static final long serialVersionUID = 1155158530L;

    public static final QEos2 eos2 = new QEos2("eos2");

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

    public QEos2(String variable) {
        super(Eos2.class, forVariable(variable));
    }

    public QEos2(Path<? extends Eos2> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEos2(PathMetadata metadata) {
        super(Eos2.class, metadata);
    }

}

