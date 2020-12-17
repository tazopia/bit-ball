package spoon.sun.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSunId is a Querydsl query type for SunId
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSunId extends EntityPathBase<SunId> {

    private static final long serialVersionUID = -1145358277L;

    public static final QSunId sunId = new QSunId("sunId");

    public final StringPath gnum = createString("gnum");

    public final StringPath userid = createString("userid");

    public QSunId(String variable) {
        super(SunId.class, forVariable(variable));
    }

    public QSunId(Path<? extends SunId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSunId(PathMetadata metadata) {
        super(SunId.class, metadata);
    }

}

