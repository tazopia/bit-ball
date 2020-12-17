package spoon.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 2130808206L;

    public static final QMember member = new QMember("member1");

    public final StringPath account = createString("account");

    public final StringPath agency1 = createString("agency1");

    public final StringPath agency2 = createString("agency2");

    public final StringPath agency3 = createString("agency3");

    public final StringPath agency4 = createString("agency4");

    public final BooleanPath balanceAladdin = createBoolean("balanceAladdin");

    public final BooleanPath balanceDari = createBoolean("balanceDari");

    public final BooleanPath balanceLadder = createBoolean("balanceLadder");

    public final BooleanPath balanceLowhi = createBoolean("balanceLowhi");

    public final BooleanPath balancePower = createBoolean("balancePower");

    public final StringPath bank = createString("bank");

    public final StringPath bankPassword = createString("bankPassword");

    public final NumberPath<Long> betCntTotal = createNumber("betCntTotal", Long.class);

    public final DateTimePath<java.util.Date> betDate = createDateTime("betDate", java.util.Date.class);

    public final NumberPath<Long> betSports = createNumber("betSports", Long.class);

    public final NumberPath<Long> betSportsCnt = createNumber("betSportsCnt", Long.class);

    public final NumberPath<Long> betTotal = createNumber("betTotal", Long.class);

    public final NumberPath<Long> betZone = createNumber("betZone", Long.class);

    public final NumberPath<Long> betZoneCnt = createNumber("betZoneCnt", Long.class);

    public final BooleanPath black = createBoolean("black");

    public final BooleanPath block = createBoolean("block");

    public final NumberPath<Double> btc3Max = createNumber("btc3Max", Double.class);

    public final NumberPath<Double> btc3Min = createNumber("btc3Min", Double.class);

    public final NumberPath<Double> btc5Max = createNumber("btc5Max", Double.class);

    public final NumberPath<Double> btc5Min = createNumber("btc5Min", Double.class);

    public final NumberPath<Long> change = createNumber("change", Long.class);

    public final NumberPath<Long> deposit = createNumber("deposit", Long.class);

    public final StringPath depositor = createString("depositor");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath joinCode = createString("joinCode");

    public final DateTimePath<java.util.Date> joinDate = createDateTime("joinDate", java.util.Date.class);

    public final StringPath joinDomain = createString("joinDomain");

    public final StringPath joinIp = createString("joinIp");

    public final NumberPath<Double> keno = createNumber("keno", Double.class);

    public final NumberPath<Double> kenoLadder = createNumber("kenoLadder", Double.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Long> loginCnt = createNumber("loginCnt", Long.class);

    public final DateTimePath<java.util.Date> loginDate = createDateTime("loginDate", java.util.Date.class);

    public final StringPath loginDevice = createString("loginDevice");

    public final StringPath loginDomain = createString("loginDomain");

    public final StringPath loginIp = createString("loginIp");

    public final StringPath memo = createString("memo");

    public final NumberPath<Long> money = createNumber("money", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath pass = createString("pass");

    public final StringPath passKey = createString("passKey");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final NumberPath<Double> powerLadder = createNumber("powerLadder", Double.class);

    public final NumberPath<Double> powerMax = createNumber("powerMax", Double.class);

    public final NumberPath<Double> powerMin = createNumber("powerMin", Double.class);

    public final StringPath rateCode = createString("rateCode");

    public final NumberPath<Double> rateShare = createNumber("rateShare", Double.class);

    public final NumberPath<Double> rateSports = createNumber("rateSports", Double.class);

    public final NumberPath<Double> rateZone = createNumber("rateZone", Double.class);

    public final StringPath recommender = createString("recommender");

    public final EnumPath<spoon.member.domain.Role> role = createEnum("role", spoon.member.domain.Role.class);

    public final BooleanPath secession = createBoolean("secession");

    public final StringPath userid = createString("userid");

    public final NumberPath<Long> withdraw = createNumber("withdraw", Long.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

