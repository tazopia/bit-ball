package spoon.gameZone.luck;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLuck is a Querydsl query type for Luck
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLuck extends EntityPathBase<Luck> {

    private static final long serialVersionUID = 272020155L;

    public static final QLuck luck = new QLuck("luck");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final StringPath dealer = createString("dealer");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<double[], Double> odds = createArray("odds", double[].class);

    public final StringPath player1 = createString("player1");

    public final StringPath player2 = createString("player2");

    public final StringPath player3 = createString("player3");

    public final StringPath result = createString("result");

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final StringPath sdate = createString("sdate");

    public QLuck(String variable) {
        super(Luck.class, forVariable(variable));
    }

    public QLuck(Path<? extends Luck> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLuck(PathMetadata metadata) {
        super(Luck.class, metadata);
    }

}

