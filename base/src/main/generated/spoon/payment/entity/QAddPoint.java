package spoon.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddPoint is a Querydsl query type for AddPoint
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAddPoint extends EntityPathBase<AddPoint> {

    private static final long serialVersionUID = 1635942261L;

    public static final QAddPoint addPoint = new QAddPoint("addPoint");

    public final NumberPath<Long> actionId = createNumber("actionId", Long.class);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath memo = createString("memo");

    public final EnumPath<spoon.payment.domain.PointCode> pointCode = createEnum("pointCode", spoon.payment.domain.PointCode.class);

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QAddPoint(String variable) {
        super(AddPoint.class, forVariable(variable));
    }

    public QAddPoint(Path<? extends AddPoint> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddPoint(PathMetadata metadata) {
        super(AddPoint.class, metadata);
    }

}

