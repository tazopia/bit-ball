package spoon.bot.support;


import org.junit.Test;

public class ZoneMakerTest {

    @Test
    public void 게임생성테스트() {
        ZoneMaker zoneMaker = new ZoneMaker(5);
        System.out.println(zoneMaker.getRound());
        System.out.println(zoneMaker.getGameDate());
    }
}