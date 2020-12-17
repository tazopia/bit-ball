package spoon.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPoint is a Querydsl query type for Point
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPoint extends EntityPathBase<Point> {

    private static final long serialVersionUID = 1779313994L;

    public static final QPoint point = new QPoint("point");

    public final NumberPath<Long> actionId = createNumber("actionId", Long.class);

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath memo = createString("memo");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> orgPoint = createNumber("orgPoint", Long.class);

    public final EnumPath<spoon.payment.domain.PointCode> pointCode = createEnum("pointCode", spoon.payment.domain.PointCode.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QPoint(String variable) {
        super(Point.class, forVariable(variable));
    }

    public QPoint(Path<? extends Point> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPoint(PathMetadata metadata) {
        super(Point.class, metadata);
    }

}

