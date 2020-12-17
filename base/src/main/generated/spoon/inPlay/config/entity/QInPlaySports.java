package spoon.inPlay.config.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlaySports is a Querydsl query type for InPlaySports
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlaySports extends EntityPathBase<InPlaySports> {

    private static final long serialVersionUID = 1951570927L;

    public static final QInPlaySports inPlaySports = new QInPlaySports("inPlaySports");

    public final StringPath flag = createString("flag");

    public final StringPath korName = createString("korName");

    public final StringPath name = createString("name");

    public QInPlaySports(String variable) {
        super(InPlaySports.class, forVariable(variable));
    }

    public QInPlaySports(Path<? extends InPlaySports> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlaySports(PathMetadata metadata) {
        super(InPlaySports.class, metadata);
    }

}

