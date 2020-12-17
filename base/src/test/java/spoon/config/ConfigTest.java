package spoon.config;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import spoon.config.domain.Config;
import spoon.config.enumeration.Edition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@EnableConfigurationProperties
public class ConfigTest {

    @Test
    public void bankTest() {
        assertThat("국민은행", is(Config.getBanks()[0]));
        log.debug("은행명 : {}", Joiner.on(",").join(Config.getBanks()));
    }

    @Test
    public void editionTest() {
        assertThat(Edition.BASIC, is(Config.getEdition()));
    }

}