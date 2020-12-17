package spoon.seller.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QJoinCode is a Querydsl query type for JoinCode
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJoinCode extends EntityPathBase<JoinCode> {

    private static final long serialVersionUID = -534918384L;

    public static final QJoinCode joinCode = new QJoinCode("joinCode");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final StringPath code = createString("code");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public QJoinCode(String variable) {
        super(JoinCode.class, forVariable(variable));
    }

    public QJoinCode(Path<? extends JoinCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJoinCode(PathMetadata metadata) {
        super(JoinCode.class, metadata);
    }

}

