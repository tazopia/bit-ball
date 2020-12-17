package spoon.inPlay.config.domain;

import lombok.Getter;
import org.springframework.stereotype.Component;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlayMarket;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;
import spoon.inPlay.support.extend.OptionalMap;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InPlayConfig {

    /**
     * 서버 접속 정보
     */
    @Getter
    private static final String host = "103.1.250.198";

    @Getter
    private static final int port = 5672;

    @Getter
    private static final String username = "consumer-0015";

    @Getter
    private static final String password = "betip4484";

    // exchange
    @Getter
    private static final String exchange = "exchange-0015";

    // 10초마다 경기 정보를 받아 오는 곳
    @Getter
    private static final String gameUrl = "http://odds-info.net/inplay/getlist.php";


    /**
     * 스포츠, 리그, 팀명 정보
     */
    @Getter
    private static final OptionalMap<InPlaySports> sports = new OptionalMap<>();

    @Getter
    private static final OptionalMap<InPlayLeague> league = new OptionalMap<>();

    @Getter
    private static final OptionalMap<InPlayTeam> team = new OptionalMap<>();

    /**
     * 게임정보
     */
    @Getter
    private static final Map<Long, String> score = new LinkedHashMap<>();

    @Getter
    private static final Map<Long, InPlayMarket> market = new LinkedHashMap<>();
}
