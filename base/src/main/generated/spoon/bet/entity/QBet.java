package spoon.bet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBet is a Querydsl query type for Bet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBet extends EntityPathBase<Bet> {

    private static final long serialVersionUID = -990264864L;

    public static final QBet bet = new QBet("bet");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath balance = createBoolean("balance");

    public final NumberPath<Integer> betCount = createNumber("betCount", Integer.class);

    public final DateTimePath<java.util.Date> betDate = createDateTime("betDate", java.util.Date.class);

    public final ListPath<BetItem, QBetItem> betItems = this.<BetItem, QBetItem>createList("betItems", BetItem.class, QBetItem.class, PathInits.DIRECT2);

    public final NumberPath<Long> betMoney = createNumber("betMoney", Long.class);

    public final NumberPath<Double> betOdds = createNumber("betOdds", Double.class);

    public final BooleanPath black = createBoolean("black");

    public final BooleanPath cancel = createBoolean("cancel");

    public final NumberPath<Integer> cancelCount = createNumber("cancelCount", Integer.class);

    public final BooleanPath closing = createBoolean("closing");

    public final DateTimePath<java.util.Date> closingDate = createDateTime("closingDate", java.util.Date.class);

    public final BooleanPath deleted = createBoolean("deleted");

    public final BooleanPath enabled = createBoolean("enabled");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final NumberPath<Double> eventOdds = createNumber("eventOdds", Double.class);

    public final NumberPath<Long> expMoney = createNumber("expMoney", Long.class);

    public final NumberPath<Integer> hitCount = createNumber("hitCount", Integer.class);

    public final NumberPath<Long> hitMoney = createNumber("hitMoney", Long.class);

    public final NumberPath<Double> hitOdds = createNumber("hitOdds", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Integer> loseCount = createNumber("loseCount", Integer.class);

    public final EnumPath<spoon.game.domain.MenuCode> menuCode = createEnum("menuCode", spoon.game.domain.MenuCode.class);

    public final StringPath nickname = createString("nickname");

    public final BooleanPath payment = createBoolean("payment");

    public final StringPath recommender = createString("recommender");

    public final StringPath result = createString("result");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final DateTimePath<java.util.Date> startDate = createDateTime("startDate", java.util.Date.class);

    public final StringPath userid = createString("userid");

    public QBet(String variable) {
        super(Bet.class, forVariable(variable));
    }

    public QBet(Path<? extends Bet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBet(PathMetadata metadata) {
        super(Bet.class, metadata);
    }

}

