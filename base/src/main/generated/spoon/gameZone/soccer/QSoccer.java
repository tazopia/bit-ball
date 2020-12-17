package spoon.gameZone.soccer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSoccer is a Querydsl query type for Soccer
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSoccer extends EntityPathBase<Soccer> {

    private static final long serialVersionUID = 274205179L;

    public static final QSoccer soccer = new QSoccer("soccer");

    public final StringPath ah = createString("ah");

    public final NumberPath<Double> ahAway = createNumber("ahAway", Double.class);

    public final NumberPath<Double> ahDraw = createNumber("ahDraw", Double.class);

    public final NumberPath<Double> ahHome = createNumber("ahHome", Double.class);

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath league = createString("league");

    public final StringPath ma = createString("ma");

    public final NumberPath<Double> maAway = createNumber("maAway", Double.class);

    public final NumberPath<Double> maDraw = createNumber("maDraw", Double.class);

    public final NumberPath<Double> maHome = createNumber("maHome", Double.class);

    public final StringPath ou = createString("ou");

    public final NumberPath<Double> ouAway = createNumber("ouAway", Double.class);

    public final NumberPath<Double> ouDraw = createNumber("ouDraw", Double.class);

    public final NumberPath<Double> ouHome = createNumber("ouHome", Double.class);

    public final NumberPath<Integer> scoreAway = createNumber("scoreAway", Integer.class);

    public final NumberPath<Integer> scoreHome = createNumber("scoreHome", Integer.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath teamAway = createString("teamAway");

    public final StringPath teamHome = createString("teamHome");

    public QSoccer(String variable) {
        super(Soccer.class, forVariable(variable));
    }

    public QSoccer(Path<? extends Soccer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSoccer(PathMetadata metadata) {
        super(Soccer.class, metadata);
    }

}

