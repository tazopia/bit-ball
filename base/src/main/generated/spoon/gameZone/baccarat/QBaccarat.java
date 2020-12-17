package spoon.gameZone.baccarat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaccarat is a Querydsl query type for Baccarat
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBaccarat extends EntityPathBase<Baccarat> {

    private static final long serialVersionUID = -177005701L;

    public static final QBaccarat baccarat = new QBaccarat("baccarat");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final StringPath b1 = createString("b1");

    public final StringPath b2 = createString("b2");

    public final StringPath b3 = createString("b3");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath p1 = createString("p1");

    public final StringPath p2 = createString("p2");

    public final StringPath p3 = createString("p3");

    public final StringPath result = createString("result");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QBaccarat(String variable) {
        super(Baccarat.class, forVariable(variable));
    }

    public QBaccarat(Path<? extends Baccarat> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaccarat(PathMetadata metadata) {
        super(Baccarat.class, metadata);
    }

}

