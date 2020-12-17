package spoon.inPlay.config.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayMarket is a Querydsl query type for InPlayMarket
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayMarket extends EntityPathBase<InPlayMarket> {

    private static final long serialVersionUID = 1766025388L;

    public static final QInPlayMarket inPlayMarket = new QInPlayMarket("inPlayMarket");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath korName = createString("korName");

    public final NumberPath<Integer> line = createNumber("line", Integer.class);

    public final StringPath menu = createString("menu");

    public final SimplePath<spoon.inPlay.config.domain.InPlayMinMax> minMax = createSimple("minMax", spoon.inPlay.config.domain.InPlayMinMax.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public final BooleanPath team = createBoolean("team");

    public QInPlayMarket(String variable) {
        super(InPlayMarket.class, forVariable(variable));
    }

    public QInPlayMarket(Path<? extends InPlayMarket> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayMarket(PathMetadata metadata) {
        super(InPlayMarket.class, metadata);
    }

}

