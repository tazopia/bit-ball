package spoon.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLoginHistory is a Querydsl query type for LoginHistory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLoginHistory extends EntityPathBase<LoginHistory> {

    private static final long serialVersionUID = 1893273823L;

    public static final QLoginHistory loginHistory = new QLoginHistory("loginHistory");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final StringPath device = createString("device");

    public final StringPath domain = createString("domain");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final DateTimePath<java.util.Date> loginDate = createDateTime("loginDate", java.util.Date.class);

    public final DatePath<java.util.Date> loginDay = createDate("loginDay", java.util.Date.class);

    public final StringPath nickname = createString("nickname");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath userid = createString("userid");

    public QLoginHistory(String variable) {
        super(LoginHistory.class, forVariable(variable));
    }

    public QLoginHistory(Path<? extends LoginHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLoginHistory(PathMetadata metadata) {
        super(LoginHistory.class, metadata);
    }

}

