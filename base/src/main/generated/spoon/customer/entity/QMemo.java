package spoon.customer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemo is a Querydsl query type for Memo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMemo extends EntityPathBase<Memo> {

    private static final long serialVersionUID = -1494619854L;

    public static final QMemo memo = new QMemo("memo");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath checked = createBoolean("checked");

    public final StringPath contents = createString("contents");

    public final BooleanPath enabled = createBoolean("enabled");

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath title = createString("title");

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QMemo(String variable) {
        super(Memo.class, forVariable(variable));
    }

    public QMemo(Path<? extends Memo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemo(PathMetadata metadata) {
        super(Memo.class, metadata);
    }

}

