package spoon.gameZone.aladdin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAladdin is a Querydsl query type for Aladdin
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAladdin extends EntityPathBase<Aladdin> {

    private static final long serialVersionUID = -31916427L;

    public static final QAladdin aladdin = new QAladdin("aladdin");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath line = createString("line");

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath start = createString("start");

    public QAladdin(String variable) {
        super(Aladdin.class, forVariable(variable));
    }

    public QAladdin(Path<? extends Aladdin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAladdin(PathMetadata metadata) {
        super(Aladdin.class, metadata);
    }

}

