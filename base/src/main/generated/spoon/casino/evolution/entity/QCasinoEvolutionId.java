package spoon.casino.evolution.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCasinoEvolutionId is a Querydsl query type for CasinoEvolutionId
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCasinoEvolutionId extends EntityPathBase<CasinoEvolutionId> {

    private static final long serialVersionUID = -1792460187L;

    public static final QCasinoEvolutionId casinoEvolutionId = new QCasinoEvolutionId("casinoEvolutionId");

    public final StringPath casinoid = createString("casinoid");

    public final StringPath userid = createString("userid");

    public QCasinoEvolutionId(String variable) {
        super(CasinoEvolutionId.class, forVariable(variable));
    }

    public QCasinoEvolutionId(Path<? extends CasinoEvolutionId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCasinoEvolutionId(PathMetadata metadata) {
        super(CasinoEvolutionId.class, metadata);
    }

}

