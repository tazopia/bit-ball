package spoon.inPlay.config.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInPlayTeam is a Querydsl query type for InPlayTeam
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInPlayTeam extends EntityPathBase<InPlayTeam> {

    private static final long serialVersionUID = -176721203L;

    public static final QInPlayTeam inPlayTeam = new QInPlayTeam("inPlayTeam");

    public final StringPath korName = createString("korName");

    public final StringPath league = createString("league");

    public final StringPath name = createString("name");

    public final StringPath sports = createString("sports");

    public QInPlayTeam(String variable) {
        super(InPlayTeam.class, forVariable(variable));
    }

    public QInPlayTeam(Path<? extends InPlayTeam> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInPlayTeam(PathMetadata metadata) {
        super(InPlayTeam.class, metadata);
    }

}

