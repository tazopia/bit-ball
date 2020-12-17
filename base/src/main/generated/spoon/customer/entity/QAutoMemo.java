package spoon.customer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAutoMemo is a Querydsl query type for AutoMemo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAutoMemo extends EntityPathBase<AutoMemo> {

    private static final long serialVersionUID = 1551503233L;

    public static final QAutoMemo autoMemo = new QAutoMemo("autoMemo");

    public final StringPath code = createString("code");

    public final StringPath contents = createString("contents");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath title = createString("title");

    public QAutoMemo(String variable) {
        super(AutoMemo.class, forVariable(variable));
    }

    public QAutoMemo(Path<? extends AutoMemo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAutoMemo(PathMetadata metadata) {
        super(AutoMemo.class, metadata);
    }

}

