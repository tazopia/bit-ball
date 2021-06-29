package spoon.casino.evo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCasinoEvoUser is a Querydsl query type for CasinoEvoUser
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCasinoEvoUser extends EntityPathBase<CasinoEvoUser> {

    private static final long serialVersionUID = -778367691L;

    public static final QCasinoEvoUser casinoEvoUser = new QCasinoEvoUser("casinoEvoUser");

    public final StringPath casinoId = createString("casinoId");

    public final BooleanPath success = createBoolean("success");

    public final StringPath userid = createString("userid");

    public QCasinoEvoUser(String variable) {
        super(CasinoEvoUser.class, forVariable(variable));
    }

    public QCasinoEvoUser(Path<? extends CasinoEvoUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCasinoEvoUser(PathMetadata metadata) {
        super(CasinoEvoUser.class, metadata);
    }

}

