package spoon.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSports is a Querydsl query type for Sports
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSports extends EntityPathBase<Sports> {

    private static final long serialVersionUID = 470342923L;

    public static final QSports sports = new QSports("sports");

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public final StringPath sportsFlag = createString("sportsFlag");

    public final StringPath sportsName = createString("sportsName");

    public QSports(String variable) {
        super(Sports.class, forVariable(variable));
    }

    public QSports(Path<? extends Sports> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSports(PathMetadata metadata) {
        super(Sports.class, metadata);
    }

}

