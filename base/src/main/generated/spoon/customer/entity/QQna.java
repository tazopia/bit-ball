package spoon.customer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QQna is a Querydsl query type for Qna
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QQna extends EntityPathBase<Qna> {

    private static final long serialVersionUID = 228885228L;

    public static final QQna qna = new QQna("qna");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath alarm = createBoolean("alarm");

    public final BooleanPath bet = createBoolean("bet");

    public final ArrayPath<long[], Long> betId = createArray("betId", long[].class);

    public final BooleanPath checked = createBoolean("checked");

    public final StringPath contents = createString("contents");

    public final BooleanPath enabled = createBoolean("enabled");

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final BooleanPath re = createBoolean("re");

    public final DateTimePath<java.util.Date> reDate = createDateTime("reDate", java.util.Date.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath reply = createString("reply");

    public final StringPath reTitle = createString("reTitle");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath title = createString("title");

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QQna(String variable) {
        super(Qna.class, forVariable(variable));
    }

    public QQna(Path<? extends Qna> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQna(PathMetadata metadata) {
        super(Qna.class, metadata);
    }

}

