package spoon.gameZone.keno.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QKeno is a Querydsl query type for Keno
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QKeno extends EntityPathBase<Keno> {

    private static final long serialVersionUID = 2006552486L;

    public static final QKeno keno = new QKeno("keno");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath overunder = createString("overunder");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public final NumberPath<Integer> sum = createNumber("sum", Integer.class);

    public QKeno(String variable) {
        super(Keno.class, forVariable(variable));
    }

    public QKeno(Path<? extends Keno> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKeno(PathMetadata metadata) {
        super(Keno.class, metadata);
    }

}

