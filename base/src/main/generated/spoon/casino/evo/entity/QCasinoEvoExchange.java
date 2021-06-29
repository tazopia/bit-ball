package spoon.casino.evo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCasinoEvoExchange is a Querydsl query type for CasinoEvoExchange
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCasinoEvoExchange extends EntityPathBase<CasinoEvoExchange> {

    private static final long serialVersionUID = -858068339L;

    public static final QCasinoEvoExchange casinoEvoExchange = new QCasinoEvoExchange("casinoEvoExchange");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final StringPath casinoId = createString("casinoId");

    public final StringPath gameType = createString("gameType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath round = createString("round");

    public final StringPath trans = createString("trans");

    public final StringPath type = createString("type");

    public final StringPath userid = createString("userid");

    public QCasinoEvoExchange(String variable) {
        super(CasinoEvoExchange.class, forVariable(variable));
    }

    public QCasinoEvoExchange(Path<? extends CasinoEvoExchange> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCasinoEvoExchange(PathMetadata metadata) {
        super(CasinoEvoExchange.class, metadata);
    }

}

