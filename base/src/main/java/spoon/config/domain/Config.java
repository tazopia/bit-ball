package spoon.config.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import spoon.config.entity.JsonConfig;
import spoon.config.enumeration.Edition;
import spoon.customer.entity.AutoMemo;
import spoon.game.entity.League;
import spoon.game.entity.Sports;
import spoon.game.entity.Team;
import spoon.support.extend.DefaultHashMap;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "config")
public class Config {

    @Getter
    @Setter
    private static Edition edition;

    @Getter
    @Setter
    private static String version;

    @Getter
    @Setter
    private static String god;

    @Getter
    @Setter
    private static String pathJoin;

    @Getter
    @Setter
    private static String pathSite;

    @Getter
    @Setter
    private static String pathAdmin;

    @Getter
    @Setter
    private static String pathSeller;

    @Getter
    @Setter
    private static String[] banks;

    @Getter
    @Setter
    private static SysConfig sysConfig;

    @Getter
    @Setter
    private static GameConfig gameConfig;

    @Getter
    @Setter
    private static SiteConfig siteConfig;

    @Getter
    @Setter
    private static EventConfig eventConfig;

    @Getter
    @Setter
    private static EventLottoConfig eventLottoConfig;

    @Getter
    @Setter
    private static JsonConfig rolling;

    @Getter
    @Setter
    private static AutoMemo joinMemo;

    @Getter
    @Setter
    private static Map<Long, AutoMemo> autoMemoMap = new DefaultHashMap<>(new AutoMemo());

    @Getter
    @Setter
    private static Map<String, Sports> sportsMap = new DefaultHashMap<>(new Sports());

    @Getter
    @Setter
    private static Map<String, League> leagueMap = new DefaultHashMap<>(new League());

    @Getter
    @Setter
    private static Map<String, Team> teamMap = new DefaultHashMap<>(new Team());

}
