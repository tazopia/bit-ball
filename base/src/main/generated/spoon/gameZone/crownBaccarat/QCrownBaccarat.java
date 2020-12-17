package spoon.gameZone.crownBaccarat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCrownBaccarat is a Querydsl query type for CrownBaccarat
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrownBaccarat extends EntityPathBase<CrownBaccarat> {

    private static final long serialVersionUID = -1754795725L;

    public static final QCrownBaccarat crownBaccarat = new QCrownBaccarat("crownBaccarat");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final StringPath b = createString("b");

    public final StringPath c = createString("c");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath p = createString("p");

    public final StringPath result = createString("result");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QCrownBaccarat(String variable) {
        super(CrownBaccarat.class, forVariable(variable));
    }

    public QCrownBaccarat(Path<? extends CrownBaccarat> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrownBaccarat(PathMetadata metadata) {
        super(CrownBaccarat.class, metadata);
    }

}

