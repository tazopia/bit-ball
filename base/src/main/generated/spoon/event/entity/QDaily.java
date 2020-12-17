package spoon.event.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDaily is a Querydsl query type for Daily
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDaily extends EntityPathBase<Daily> {

    private static final long serialVersionUID = 1703842047L;

    public static final QDaily daily1 = new QDaily("daily1");

    public final NumberPath<Integer> daily = createNumber("daily", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath sdate = createString("sdate");

    public final StringPath userid = createString("userid");

    public QDaily(String variable) {
        super(Daily.class, forVariable(variable));
    }

    public QDaily(Path<? extends Daily> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDaily(PathMetadata metadata) {
        super(Daily.class, metadata);
    }

}

