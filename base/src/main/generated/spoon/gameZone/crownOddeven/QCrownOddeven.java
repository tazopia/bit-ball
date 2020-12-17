package spoon.gameZone.crownOddeven;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCrownOddeven is a Querydsl query type for CrownOddeven
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrownOddeven extends EntityPathBase<CrownOddeven> {

    private static final long serialVersionUID = 1438060891L;

    public static final QCrownOddeven crownOddeven = new QCrownOddeven("crownOddeven");

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

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QCrownOddeven(String variable) {
        super(CrownOddeven.class, forVariable(variable));
    }

    public QCrownOddeven(Path<? extends CrownOddeven> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrownOddeven(PathMetadata metadata) {
        super(CrownOddeven.class, metadata);
    }

}

