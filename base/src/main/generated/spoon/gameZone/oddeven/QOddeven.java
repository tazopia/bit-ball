package spoon.gameZone.oddeven;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QOddeven is a Querydsl query type for Oddeven
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QOddeven extends EntityPathBase<Oddeven> {

    private static final long serialVersionUID = 711949713L;

    public static final QOddeven oddeven1 = new QOddeven("oddeven1");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final StringPath card1 = createString("card1");

    public final StringPath card2 = createString("card2");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath oddeven = createString("oddeven");

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath overunder = createString("overunder");

    public final StringPath pattern = createString("pattern");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QOddeven(String variable) {
        super(Oddeven.class, forVariable(variable));
    }

    public QOddeven(Path<? extends Oddeven> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOddeven(PathMetadata metadata) {
        super(Oddeven.class, metadata);
    }

}

