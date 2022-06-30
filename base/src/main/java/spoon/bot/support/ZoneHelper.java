package spoon.bot.support;

import org.springframework.util.Assert;
import spoon.game.domain.GameResult;
import spoon.gameZone.ZoneScore;

public class ZoneHelper {

    public static ZoneScore zoneResult(String result) {
        Assert.notNull(result, "결과는 null 이 될 수 없습니다.");

        switch (result) {
            case "ODD":
            case "O":
            case "LEFT":
            case "3":
            case "p1":
            case "OVER":
            case "대":
            case "LOW":
            case "1": // 7luck
            case "odd":
            case "over":
            case "B":
                return new ZoneScore(1, 0, GameResult.HOME);
            case "p2":
            case "중":
            case "2": // 7luck
            case "M":
                return new ZoneScore(1, 1, GameResult.DRAW);
            case "EVEN":
            case "E":
            case "RIGHT":
            case "4":
            case "p3":
            case "UNDER":
            case "소":
            case "HI":
            case "0": // 7luck
            case "even":
            case "under":
            case "S":
                return new ZoneScore(0, 1, GameResult.AWAY);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore line3StartResult(String line, String start) {
        switch (zoneResult(line).getGameResult()) {
            case HOME:
                return zoneResult(start);
            case AWAY:
                return new ZoneScore(0, 0, GameResult.NONE);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore line4StartResult(String line, String start) {
        switch (zoneResult(line).getGameResult()) {
            case HOME:
                return new ZoneScore(0, 0, GameResult.NONE);
            case AWAY:
                return zoneResult(start);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore lowOddevenResult(String lowhi, String oddeven) {
        switch (zoneResult(lowhi).getGameResult()) {
            case HOME:
                return zoneResult(oddeven);
            case AWAY:
                return new ZoneScore(0, 0, GameResult.NONE);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore hiOddevenResult(String lowhi, String oddeven) {
        switch (zoneResult(lowhi).getGameResult()) {
            case HOME:
                return new ZoneScore(0, 0, GameResult.NONE);
            case AWAY:
                return zoneResult(oddeven);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore pattern1Result(String pattern, boolean cancel) {
        if (cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        }
        switch (pattern) {
            case "스페이드":
            case "S":
                return new ZoneScore(1, 0, GameResult.HOME);
            case "하트":
            case "H":
                return new ZoneScore(0, 1, GameResult.AWAY);
            default:
                return new ZoneScore(0, 0, GameResult.NONE);
        }
    }

    public static ZoneScore pattern2Result(String pattern, boolean cancel) {
        if (cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        }
        switch (pattern) {
            case "크로바":
            case "C":
                return new ZoneScore(1, 0, GameResult.HOME);
            case "다이아":
            case "D":
                return new ZoneScore(0, 1, GameResult.AWAY);
            default:
                return new ZoneScore(0, 0, GameResult.NONE);
        }
    }

    public static ZoneScore colorResult(String result) {
        switch (result) {
            case "D":
            case "H":
                // Red
                return new ZoneScore(1, 0, GameResult.HOME);
            case "S":
            case "C":
                // Black
                return new ZoneScore(0, 1, GameResult.AWAY);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore zoneResultDraw(String result) {
        switch (result) {
            case "P":
            case "1":
            case "K":
                return new ZoneScore(1, 0, GameResult.HOME);
            case "B":
            case "0":
            case "J":
                return new ZoneScore(0, 1, GameResult.AWAY);
            case "T":
            case "2":
                return new ZoneScore(1, 1, GameResult.DRAW_HIT);
            default:
                return new ZoneScore(0, 0, GameResult.CANCEL);
        }
    }

    public static ZoneScore maResult(int scoreHome, int scoreAway, boolean cancel) {
        if (cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        } else if (scoreHome > scoreAway) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.HOME);
        } else if (scoreHome < scoreAway) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.AWAY);
        }
        return new ZoneScore(scoreHome, scoreAway, GameResult.DRAW);
    }

    public static ZoneScore ahResult(int scoreHome, int scoreAway, double ahDraw, boolean cancel) {
        double result = scoreHome + ahDraw - scoreAway;
        if (cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        } else if (result > 0) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.HOME);
        } else if (result < 0) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.AWAY);
        }
        return new ZoneScore(scoreHome, scoreAway, GameResult.HIT);
    }

    public static ZoneScore ouResult(int scoreHome, int scoreAway, double ouDraw, boolean cancel) {
        double result = scoreHome + scoreAway - ouDraw;
        if (cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        } else if (result > 0) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.OVER);
        } else if (result < 0) {
            return new ZoneScore(scoreHome, scoreAway, GameResult.UNDER);
        }
        return new ZoneScore(scoreHome, scoreAway, GameResult.HIT);
    }
}
