package spoon.config.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QJsonConfig is a Querydsl query type for JsonConfig
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJsonConfig extends EntityPathBase<JsonConfig> {

    private static final long serialVersionUID = -1078553114L;

    public static final QJsonConfig jsonConfig = new QJsonConfig("jsonConfig");

    public final StringPath code = createString("code");

    public final StringPath json = createString("json");

    public QJsonConfig(String variable) {
        super(JsonConfig.class, forVariable(variable));
    }

    public QJsonConfig(Path<? extends JsonConfig> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJsonConfig(PathMetadata metadata) {
        super(JsonConfig.class, metadata);
    }

}

