package spoon.sale.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSale is a Querydsl query type for Sale
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSale extends EntityPathBase<Sale> {

    private static final long serialVersionUID = -239376984L;

    public static final QSale sale = new QSale("sale");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath closing = createBoolean("closing");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final ListPath<SaleItem, QSaleItem> saleItems = this.<SaleItem, QSaleItem>createList("saleItems", SaleItem.class, QSaleItem.class, PathInits.DIRECT2);

    public final StringPath userid = createString("userid");

    public QSale(String variable) {
        super(Sale.class, forVariable(variable));
    }

    public QSale(Path<? extends Sale> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSale(PathMetadata metadata) {
        super(Sale.class, metadata);
    }

}

