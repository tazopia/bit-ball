package spoon.support.web;

import org.springframework.web.util.HtmlUtils;
import spoon.bet.entity.BetItem;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameResult;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlayMarket;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class CustomTag {

    // String tag
    public static String onlyBr(String value) {
        if (value == null) return "";
        value = HtmlUtils.htmlEscape(value);
        return value.replaceAll(System.getProperty("line.separator"), "<br/>");
    }

    public static String colorBr(String value) {
        if (value == null) return "";
        return color(value)
                .replaceAll(System.getProperty("line.separator"), "<br/>");
    }

    public static String memo(String value) {
        CurrentUser user = WebUtils.user();
        return colorBr(value).replaceAll("#아이디#", user.getUserid())
                .replaceAll("#닉네임#", user.getNickname())
                .replaceAll("#회사#", Config.getSiteConfig().getCompanyName());
    }

    public static String color(String value) {
        if (value == null) return "";
        return value
                .replaceAll("\\{\\{\\{", "<em class=\"txt3\">").replaceAll("}}}", "</em>")
                .replaceAll("\\{\\{", "<em class=\"txt2\">").replaceAll("}}", "</em>")
                .replaceAll("\\{", "<em class=\"txt1\">").replaceAll("}", "</em>");
    }

    public static String country(String ip) {
        return WebUtils.country(ip);
    }

    // Flag
    public static String sportsFlag(BetItem item) {
        switch (item.getMenuCode()) {
            case MATCH:
            case HANDICAP:
            case CROSS:
            case SPECIAL:
            case LIVE:
            case SPORTS:
                return "/images/sports/" + Config.getSportsMap().get(item.getSports().toLowerCase()).getSportsFlag();
            default:
                return "/images/zone/flag-" + item.getMenuCode().toString().toLowerCase() + ".png";
        }
    }

    public static String leagueFlag(BetItem item) {
        switch (item.getMenuCode()) {
            case MATCH:
            case HANDICAP:
            case CROSS:
            case SPECIAL:
            case LIVE:
            case SPORTS:
                return "/images/league/" + Config.getLeagueMap().get((item.getSports() + "-" + item.getLeague()).toLowerCase()).getLeagueFlag();
            default:
                return "/images/zone/flag-" + item.getMenuCode().toString().toLowerCase() + ".png";
        }
    }

    // Date tag
    public static String dayWeekTime(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "MM/dd(E) ") + "<em class=\"color02\">" + DateUtils.format(date, "HH:mm") + "</em>";
    }

    public static String dayWeekTimes(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "MM/dd(E) ") + "<em class=\"color02\">" + DateUtils.format(date, "HH:mm:ss") + "</em>";
    }

    public static String dayWeekTimes(LocalDateTime date) {
        if (date == null) return "-";
        return date.format(DateTimeFormatter.ofPattern("MM/dd(E) ")) + "<em class=\"color02\">" + date.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "</em>";
    }

    public static String dayWeek(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "MM/dd(E)");
    }

    public static String dateWeek(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "yyyy.MM.dd ") + "<em class=\"color02\">" + DateUtils.format(date, "(E)") + "</em>";
    }

    public static String titleDate(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "yyyy.MM.dd(E) HH:mm:ss");
    }

    public static String fullDate(Date date) {
        if (date == null) return "-";
        return "<em class=\"color04\">" + DateUtils.format(date, "yyyy.MM.dd") + "</em><em class=\"color01\">" + DateUtils.format(date, " (E) ") + "</em>" + DateUtils.format(date, "HH:mm:ss");
    }

    public static String betDate(Date date) {
        if (date == null) return "-";
        return DateUtils.format(date, "dd(E) ") + "<em class=\"color02\">" + DateUtils.format(date, "HH:mm:ss") + "</em>";
    }

    // Number Tag
    public static String num(Long num) {
        if (num == null) return "0";
        return String.format("%,d", num);
    }

    public static int numVal(BigDecimal num) {
        return num == null ? 0 : num.intValue();
    }

    public static String handicap(double num) {
        return String.format("%,.1f", num);
    }

    public static String odds(double num) {
        return String.format("%,.2f", num);
    }

    public static String odds(double odds, double handicap) {
        if (odds == 0D && handicap == 0D) {
            return "VS";
        } else if (odds > 0) {
            return odds(odds);
        } else {
            return handicap(handicap);
        }
    }

    public static String round(int round, int size) {
        return String.format("%0" + size + "d", round);
    }

    // role
    public static boolean isAdmin(Role role) {
        return role.getGroup() == Role.ADMIN.getGroup();
    }

    // recommRate
    public static double recommOddsSports(int level) {
        return Config.getGameConfig().getNoHitSportsRecommOdds()[level];
    }

    public static double recommOddsZone(int level) {
        return Config.getGameConfig().getNoHitZoneRecommOdds()[level];
    }

    // betting
    public static boolean winHome(GameResult result) {
        switch (result) {
            case HOME:
            case OVER:
                return true;
            default:
                return false;
        }
    }

    public static boolean winDraw(GameResult result) {
        switch (result) {
            case DRAW:
            case HIT:
            case DRAW_HIT:
                return true;
            default:
                return false;
        }
    }

    public static boolean winAway(GameResult result) {
        switch (result) {
            case AWAY:
            case UNDER:
                return true;
            default:
                return false;
        }
    }

    /**
     * In Play 태그
     */

    public static String sportsFlag(String name) {
        return InPlayConfig.getSports().optional(name).map(InPlaySports::getFlag).orElse("sports.png");
    }

    public static String leagueFlag(String name) {
        return InPlayConfig.getLeague().optional(name).map(InPlayLeague::getFlag).orElse("league.gif");
    }

    public static InPlayMarket market(long id) {
        return InPlayConfig.getMarket().get(id);
    }

    public static String sports(String name) {
        return InPlayConfig.getSports().optional(name).map(InPlaySports::getKorName).orElse(name);
    }

    public static String league(String name) {
        return InPlayConfig.getLeague().optional(name).map(InPlayLeague::getKorName).orElse(name);
    }

    public static String team(String name) {
        return InPlayConfig.getTeam().optional(name).map(InPlayTeam::getKorName).orElse(name);
    }

    public static String name(String name, String oname) {
        if (oname.equalsIgnoreCase("score")) {
            return name;
        }

        switch (name) {
            case "1":
                return "승";
            case "2":
                return "패";
            case "X":
                return "무";
            case "Over":
                return "오버";
            case "Under":
                return "언더";
            case "Odd":
                return "홀";
            case "Even":
                return "짝";
            default:
                return name;
        }
    }
}
