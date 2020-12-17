package spoon.accounting.domain;

import org.junit.Test;

public class AccountingDtoTest {

    @Test
    public void replace() {
        String date = "2017.11.11";
        System.out.println(date.replaceAll("\\.", "-"));
    }
}