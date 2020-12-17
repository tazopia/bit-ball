package spoon.customer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPopup is a Querydsl query type for Popup
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPopup extends EntityPathBase<Popup> {

    private static final long serialVersionUID = 914496436L;

    public static final QPopup popup = new QPopup("popup");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final StringPath sdate = createString("sdate");

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public final StringPath summary = createString("summary");

    public QPopup(String variable) {
        super(Popup.class, forVariable(variable));
    }

    public QPopup(Path<? extends Popup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPopup(PathMetadata metadata) {
        super(Popup.class, metadata);
    }

}

