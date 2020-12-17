package spoon.gameZone;

import lombok.Data;
import spoon.game.domain.GameResult;

@Data
public class ZoneScore {
    private int scoreHome;
    private int scoreAway;
    private GameResult gameResult;

    public ZoneScore(int scoreHome, int scoreAway, GameResult gameResult) {
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.gameResult = gameResult;
    }
}
