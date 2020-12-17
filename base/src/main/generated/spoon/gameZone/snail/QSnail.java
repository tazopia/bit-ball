package spoon.gameZone.snail;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSnail is a Querydsl query type for Snail
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSnail extends EntityPathBase<Snail> {

    private static final long serialVersionUID = -469810351L;

    public static final QSnail snail = new QSnail("snail");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath result = createString("result");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QSnail(String variable) {
        super(Snail.class, forVariable(variable));
    }

    public QSnail(Path<? extends Snail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSnail(PathMetadata metadata) {
        super(Snail.class, metadata);
    }

}

