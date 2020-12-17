package spoon.event.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLottoPayment is a Querydsl query type for LottoPayment
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLottoPayment extends EntityPathBase<LottoPayment> {

    private static final long serialVersionUID = -1440845516L;

    public static final QLottoPayment lottoPayment = new QLottoPayment("lottoPayment");

    public final StringPath account = createString("account");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath bank = createString("bank");

    public final BooleanPath deleted = createBoolean("deleted");

    public final StringPath depositor = createString("depositor");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath userid = createString("userid");

    public QLottoPayment(String variable) {
        super(LottoPayment.class, forVariable(variable));
    }

    public QLottoPayment(Path<? extends LottoPayment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLottoPayment(PathMetadata metadata) {
        super(LottoPayment.class, metadata);
    }

}

