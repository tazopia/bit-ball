package spoon.bot.support;

import java.util.Calendar;
import java.util.Date;

public class ZoneMaker {

    private int interval;

    public ZoneMaker(int interval) {
        this.interval = interval;
    }

    public int getRound() {
        long now = new Date().getTime();
        Calendar cal = setToday();

        long ms = now - cal.getTimeInMillis();
        return (int) Math.ceil(ms / (60D * 1000D * interval));
    }

    public Date getGameDate() {
        Calendar cal = setToday();
        cal.add(Calendar.MINUTE, getRound() * interval);
        return cal.getTime();
    }

    private Calendar setToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
