package spoon.gameZone.crownSutda;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCrownSutda is a Querydsl query type for CrownSutda
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrownSutda extends EntityPathBase<CrownSutda> {

    private static final long serialVersionUID = 1222096667L;

    public static final QCrownSutda crownSutda = new QCrownSutda("crownSutda");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath j1 = createString("j1");

    public final StringPath j2 = createString("j2");

    public final StringPath japan = createString("japan");

    public final StringPath k1 = createString("k1");

    public final StringPath k2 = createString("k2");

    public final StringPath korea = createString("korea");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath result = createString("result");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QCrownSutda(String variable) {
        super(CrownSutda.class, forVariable(variable));
    }

    public QCrownSutda(Path<? extends CrownSutda> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrownSutda(PathMetadata metadata) {
        super(CrownSutda.class, metadata);
    }

}

