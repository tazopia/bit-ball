package spoon.inPlay.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import spoon.inPlay.config.domain.InPlayMarketDto;
import spoon.inPlay.config.domain.InPlayMinMax;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.support.extend.MinMaxConvert;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "INPLAY_MARKET")
public class InPlayMarket {

    @Id
    private Long id;

    private String name;

    @Nationalized
    private String korName;

    private int line;

    private int sort;

    // 매치, 오버/언더, 핸디캡, 스페셜, 기타
    @Nationalized
    @Column(length = 65)
    private String menu;

    // 노출 노출하지 않음
    private boolean enabled;

    private boolean team;

    @Convert(converter = MinMaxConvert.class)
    @Column(length = 1024)
    private InPlayMinMax minMax;

    private InPlayMarket(InPlayOdds odds) {
        this.id = odds.getMarketId();
        this.name = odds.getOname();
        this.korName = odds.getOname();
        this.line = odds.getOname().contains("1X2") ? 3 : 2;
        this.menu = "기타";
        this.minMax = new InPlayMinMax();
    }

    public static InPlayMarket of(InPlayOdds odds) {
        return new InPlayMarket(odds);
    }

    public void updateSort(int maxSort) {
        this.sort = maxSort + 1;
    }

    public void update(InPlayMarketDto.Update update) {
        this.korName = update.getKorName();
        this.line = update.getLine();
        this.menu = update.getMenu();
        this.enabled = update.isEnabled();
        this.team = update.isTeam();
        this.minMax = update.getMinMax();
    }

    public void up() {
        this.sort = this.sort - 1;
    }

    public void down() {
        this.sort = this.sort + 1;
    }
}
