package spoon.gameZone.newSnail;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QNewSnail is a Querydsl query type for NewSnail
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNewSnail extends EntityPathBase<NewSnail> {

    private static final long serialVersionUID = -1659225541L;

    public static final QNewSnail newSnail = new QNewSnail("newSnail");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath oe = createString("oe");

    public final StringPath ou = createString("ou");

    public final StringPath ranking = createString("ranking");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QNewSnail(String variable) {
        super(NewSnail.class, forVariable(variable));
    }

    public QNewSnail(Path<? extends NewSnail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNewSnail(PathMetadata metadata) {
        super(NewSnail.class, metadata);
    }

}

