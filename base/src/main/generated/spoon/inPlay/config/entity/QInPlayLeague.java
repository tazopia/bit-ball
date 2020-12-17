package spoon.inPlay.config.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayLeague is a Querydsl query type for InPlayLeague
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayLeague extends EntityPathBase<InPlayLeague> {

    private static final long serialVersionUID = 1740580511L;

    public static final QInPlayLeague inPlayLeague = new QInPlayLeague("inPlayLeague");

    public final StringPath flag = createString("flag");

    public final StringPath korName = createString("korName");

    public final StringPath location = createString("location");

    public final StringPath name = createString("name");

    public final StringPath sports = createString("sports");

    public QInPlayLeague(String variable) {
        super(InPlayLeague.class, forVariable(variable));
    }

    public QInPlayLeague(Path<? extends InPlayLeague> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayLeague(PathMetadata metadata) {
        super(InPlayLeague.class, metadata);
    }

}

