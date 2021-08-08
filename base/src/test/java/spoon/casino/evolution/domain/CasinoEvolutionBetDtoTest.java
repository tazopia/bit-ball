package spoon.casino.evolution.domain;

import junit.framework.TestCase;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CasinoEvolutionBetDtoTest extends TestCase {

    @Test
    public void testTimeFormat() {
        String date = "2021-08-07T04:09:53.000000Z".substring(0, 19);
        LocalDateTime d = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println(d);
    }
}