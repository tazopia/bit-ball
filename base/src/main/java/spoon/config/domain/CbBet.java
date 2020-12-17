package spoon.config.domain;

import lombok.Data;

/**
 * 조합베팅 조합
 */
@Data
public class CbBet {

    private boolean matchHandicap = true;
    private boolean matchOverUnder;
    private boolean handicapOverUnder;

    private boolean drawHandicap;
    private boolean drawOverUnder = true;

}
