package spoon.gameZone.dog;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDog is a Querydsl query type for Dog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDog extends EntityPathBase<Dog> {

    private static final long serialVersionUID = 1727015799L;

    public static final QDog dog = new QDog("dog");

    public final ArrayPath<long[], Long> amount = createArray("amount", long[].class);

    public final BooleanPath cancel = createBoolean("cancel");

    public final BooleanPath closing = createBoolean("closing");

    public final StringPath dog1 = createString("dog1");

    public final StringPath dog2 = createString("dog2");

    public final StringPath dog3 = createString("dog3");

    public final DateTimePath<java.util.Date> gameDate = createDateTime("gameDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath league = createString("league");

    public final NumberPath<Double> odds1 = createNumber("odds1", Double.class);

    public final NumberPath<Double> odds2 = createNumber("odds2", Double.class);

    public final NumberPath<Double> odds3 = createNumber("odds3", Double.class);

    public final NumberPath<Double> odds4 = createNumber("odds4", Double.class);

    public final NumberPath<Double> odds5 = createNumber("odds5", Double.class);

    public final NumberPath<Double> odds6 = createNumber("odds6", Double.class);

    public final StringPath sdate = createString("sdate");

    public final StringPath team1 = createString("team1");

    public final StringPath team2 = createString("team2");

    public final StringPath team3 = createString("team3");

    public final StringPath team4 = createString("team4");

    public final StringPath team5 = createString("team5");

    public final StringPath team6 = createString("team6");

    public final NumberPath<Integer> winNumber = createNumber("winNumber", Integer.class);

    public QDog(String variable) {
        super(Dog.class, forVariable(variable));
    }

    public QDog(Path<? extends Dog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDog(PathMetadata metadata) {
        super(Dog.class, metadata);
    }

}

