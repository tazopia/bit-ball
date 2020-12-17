package spoon.bot.balance.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPolygonBalance is a Querydsl query type for PolygonBalance
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPolygonBalance extends EntityPathBase<PolygonBalance> {

    private static final long serialVersionUID = 894329527L;

    public static final QPolygonBalance polygonBalance = new QPolygonBalance("polygonBalance");

    public final StringPath betType = createString("betType");

    public final StringPath game = createString("game");

    public final StringPath gameDate = createString("gameDate");

    public final StringPath gameType = createString("gameType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath round = createString("round");

    public QPolygonBalance(String variable) {
        super(PolygonBalance.class, forVariable(variable));
    }

    public QPolygonBalance(Path<? extends PolygonBalance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPolygonBalance(PathMetadata metadata) {
        super(PolygonBalance.class, metadata);
    }

}

