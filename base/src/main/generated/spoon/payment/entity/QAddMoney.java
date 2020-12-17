package spoon.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddMoney is a Querydsl query type for AddMoney
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAddMoney extends EntityPathBase<AddMoney> {

    private static final long serialVersionUID = 1633176229L;

    public static final QAddMoney addMoney = new QAddMoney("addMoney");

    public final NumberPath<Long> actionId = createNumber("actionId", Long.class);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath memo = createString("memo");

    public final EnumPath<spoon.payment.domain.MoneyCode> moneyCode = createEnum("moneyCode", spoon.payment.domain.MoneyCode.class);

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QAddMoney(String variable) {
        super(AddMoney.class, forVariable(variable));
    }

    public QAddMoney(Path<? extends AddMoney> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddMoney(PathMetadata metadata) {
        super(AddMoney.class, metadata);
    }

}

