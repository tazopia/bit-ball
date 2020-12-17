package spoon.bet.service;

import spoon.bet.domain.BetDto;
import spoon.bet.domain.BetUserRate;

import java.util.List;

public interface BetUserRateService {

    List<BetUserRate> getBetUserRate(BetDto.BetRate command);

    long getBetUserRateTotal(BetDto.BetRate command);
}
