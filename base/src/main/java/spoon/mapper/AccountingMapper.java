package spoon.mapper;

import spoon.accounting.domain.AccountingDto;
import spoon.monitor.domain.MonitorDto;

import java.util.List;

public interface AccountingMapper {

    List<AccountingDto.Daily> daily(AccountingDto.Command command);

    AccountingDto.Game gameAccount(AccountingDto.Command command);

    AccountingDto.Game gameInplay(AccountingDto.Command command);

    List<AccountingDto.Amount> money(AccountingDto.Command command);

    List<AccountingDto.Amount> point(AccountingDto.Command command);

    List<AccountingDto.Amount> board(AccountingDto.Command command);

    List<AccountingDto.Amount> comment(AccountingDto.Command command);

    MonitorDto.Amount amount(AccountingDto.Command command);

    Long fees(AccountingDto.Command command);

}
