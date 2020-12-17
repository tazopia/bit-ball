package spoon.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1503166208L;

    public static final QBoard board = new QBoard("board");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath alarm = createBoolean("alarm");

    public final BooleanPath bet = createBoolean("bet");

    public final ArrayPath<long[], Long> betId = createArray("betId", long[].class);

    public final StringPath code = createString("code");

    public final NumberPath<Long> comment = createNumber("comment", Long.class);

    public final StringPath contents = createString("contents");

    public final BooleanPath enabled = createBoolean("enabled");

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Integer> hit = createNumber("hit", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final BooleanPath showTop = createBoolean("showTop");

    public final StringPath title = createString("title");

    public final StringPath userid = createString("userid");

    public QBoard(String variable) {
        super(Board.class, forVariable(variable));
    }

    public QBoard(Path<? extends Board> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Board.class, metadata);
    }

}

