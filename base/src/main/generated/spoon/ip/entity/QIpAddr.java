package spoon.ip.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QIpAddr is a Querydsl query type for IpAddr
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIpAddr extends EntityPathBase<IpAddr> {

    private static final long serialVersionUID = 1264427769L;

    public static final QIpAddr ipAddr = new QIpAddr("ipAddr");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath worker = createString("worker");

    public QIpAddr(String variable) {
        super(IpAddr.class, forVariable(variable));
    }

    public QIpAddr(Path<? extends IpAddr> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIpAddr(PathMetadata metadata) {
        super(IpAddr.class, metadata);
    }

}

