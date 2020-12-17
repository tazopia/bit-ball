package spoon.event.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDailyPayment is a Querydsl query type for DailyPayment
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDailyPayment extends EntityPathBase<DailyPayment> {

    private static final long serialVersionUID = 898624519L;

    public static final QDailyPayment dailyPayment = new QDailyPayment("dailyPayment");

    public final StringPath account = createString("account");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath bank = createString("bank");

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final BooleanPath deleted = createBoolean("deleted");

    public final StringPath depositor = createString("depositor");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath nickname = createString("nickname");

    public final StringPath sdate = createString("sdate");

    public final StringPath userid = createString("userid");

    public QDailyPayment(String variable) {
        super(DailyPayment.class, forVariable(variable));
    }

    public QDailyPayment(Path<? extends DailyPayment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailyPayment(PathMetadata metadata) {
        super(DailyPayment.class, metadata);
    }

}

