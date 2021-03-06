package spoon.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMoney is a Querydsl query type for Money
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMoney extends EntityPathBase<Money> {

    private static final long serialVersionUID = 1776547962L;

    public static final QMoney money = new QMoney("money");

    public final NumberPath<Long> actionId = createNumber("actionId", Long.class);

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath memo = createString("memo");

    public final EnumPath<spoon.payment.domain.MoneyCode> moneyCode = createEnum("moneyCode", spoon.payment.domain.MoneyCode.class);

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> orgMoney = createNumber("orgMoney", Long.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QMoney(String variable) {
        super(Money.class, forVariable(variable));
    }

    public QMoney(Path<? extends Money> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMoney(PathMetadata metadata) {
        super(Money.class, metadata);
    }

}

