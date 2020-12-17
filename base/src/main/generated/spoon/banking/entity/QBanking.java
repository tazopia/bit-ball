package spoon.banking.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBanking is a Querydsl query type for Banking
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBanking extends EntityPathBase<Banking> {

    private static final long serialVersionUID = -699924672L;

    public static final QBanking banking = new QBanking("banking");

    public final StringPath account = createString("account");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath alarm = createBoolean("alarm");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath bank = createString("bank");

    public final EnumPath<spoon.banking.domain.BankingCode> bankingCode = createEnum("bankingCode", spoon.banking.domain.BankingCode.class);

    public final StringPath bonus = createString("bonus");

    public final NumberPath<Long> bonusPoint = createNumber("bonusPoint", Long.class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> closingDate = createDateTime("closingDate", java.util.Date.class);

    public final StringPath depositor = createString("depositor");

    public final NumberPath<Long> fees = createNumber("fees", Long.class);

    public final BooleanPath hidden = createBoolean("hidden");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Long> money = createNumber("money", Long.class);

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final BooleanPath reset = createBoolean("reset");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final StringPath rolling = createString("rolling");

    public final StringPath userid = createString("userid");

    public final StringPath worker = createString("worker");

    public QBanking(String variable) {
        super(Banking.class, forVariable(variable));
    }

    public QBanking(Path<? extends Banking> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBanking(PathMetadata metadata) {
        super(Banking.class, metadata);
    }

}

