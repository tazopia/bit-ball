package spoon.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -535272967L;

    public static final QComment comment = new QComment("comment");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> bid = createNumber("bid", Long.class);

    public final StringPath contents = createString("contents");

    public final BooleanPath enabled = createBoolean("enabled");

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath userid = createString("userid");

    public QComment(String variable) {
        super(Comment.class, forVariable(variable));
    }

    public QComment(Path<? extends Comment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QComment(PathMetadata metadata) {
        super(Comment.class, metadata);
    }

}

