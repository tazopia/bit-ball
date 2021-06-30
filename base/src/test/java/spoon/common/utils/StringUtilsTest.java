package spoon.common.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class StringUtilsTest extends TestCase {

    @Test
    public void testMd5() {
        String md5 = StringUtils.md5("I0FCBONVOVA2Z3W50G8PZS8Z5SUoperatorID=beat&session=4f579aaba484489054cf11bd5a8deeb6");
        System.out.println(md5);
    }

}