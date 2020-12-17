package spoon.sale.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSaleItem is a Querydsl query type for SaleItem
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSaleItem extends EntityPathBase<SaleItem> {

    private static final long serialVersionUID = 887308507L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSaleItem saleItem = new QSaleItem("saleItem");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final NumberPath<Long> betSports = createNumber("betSports", Long.class);

    public final NumberPath<Long> betZone = createNumber("betZone", Long.class);

    public final BooleanPath closing = createBoolean("closing");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> inMoney = createNumber("inMoney", Long.class);

    public final NumberPath<Long> lastMoney = createNumber("lastMoney", Long.class);

    public final NumberPath<Long> outMoney = createNumber("outMoney", Long.class);

    public final StringPath rateCode = createString("rateCode");

    public final NumberPath<Double> rateShare = createNumber("rateShare", Double.class);

    public final NumberPath<Double> rateSports = createNumber("rateSports", Double.class);

    public final NumberPath<Double> rateZone = createNumber("rateZone", Double.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final QSale sale;

    public final NumberPath<Long> totalMoney = createNumber("totalMoney", Long.class);

    public final StringPath userid = createString("userid");

    public final NumberPath<Long> users = createNumber("users", Long.class);

    public QSaleItem(String variable) {
        this(SaleItem.class, forVariable(variable), INITS);
    }

    public QSaleItem(Path<? extends SaleItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSaleItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSaleItem(PathMetadata metadata, PathInits inits) {
        this(SaleItem.class, metadata, inits);
    }

    public QSaleItem(Class<? extends SaleItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sale = inits.isInitialized("sale") ? new QSale(forProperty("sale")) : null;
    }

}

