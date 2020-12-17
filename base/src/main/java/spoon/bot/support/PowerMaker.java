package spoon.bot.support;

import spoon.config.domain.Config;

import java.util.Calendar;
import java.util.Date;

public class PowerMaker {

    private static final int INTERVAL = 5;

    public int getTimes() {
        long ms = new Date().getTime() - Config.getSysConfig().getZone().getPowerBase();
        int plus = (int) (ms / (INTERVAL * 60 * 1000));
        return Config.getSysConfig().getZone().getPowerRound() + plus;
    }

    public Date getGameDate(long round) {
        int times = (int) (round - Config.getSysConfig().getZone().getPowerRound()) * INTERVAL;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(Config.getSysConfig().getZone().getPowerBase()));
        cal.add(Calendar.MINUTE, times);
        return cal.getTime();
    }

    public int getRound(long round) {
        int times = (int) (Config.getSysConfig().getZone().getPowerDay() + round) % 288;
        return (times == 0) ? 288 : times;
    }

}
