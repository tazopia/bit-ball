package spoon.mapper;

import spoon.banking.domain.BankingDto;
import spoon.banking.domain.Rolling;

public interface BankingMapper {

    Rolling getRolling(String userid);

    BankingDto.Money bankingTotal(BankingDto.Date command);
}
