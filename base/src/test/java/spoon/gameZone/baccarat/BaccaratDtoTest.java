package spoon.gameZone.baccarat;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaccaratDtoTest {

    @Test
    public void 바카라결과처리() {
        String test = System.getProperty("line.separator")
                + System.getProperty("line.separator") + "A10"
                + System.getProperty("line.separator") + System.getProperty("line.separator") + "QQK";
        System.out.println(test);

        String[] result = test.split(System.getProperty("line.separator"));
        List<String> r = Stream.of(result).filter(x -> x.trim() != null && !"".equals(x)).collect(Collectors.toList());
        System.out.println(r.get(0));
        System.out.println(r.get(1));

        System.out.println(4 + 0.5 - 4);
    }

}