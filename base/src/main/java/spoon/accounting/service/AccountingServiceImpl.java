package spoon.accounting.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.accounting.domain.AccountingDto;
import spoon.game.domain.MenuCode;
import spoon.mapper.AccountingMapper;
import spoon.mapper.MonitorMapper;
import spoon.monitor.domain.MonitorDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class AccountingServiceImpl implements AccountingService {

    private AccountingMapper accountingMapper;

    private MonitorMapper monitorMapper;

    @Override
    public List<AccountingDto.Daily> daily(AccountingDto.Command command) {
        return accountingMapper.daily(command);
    }

    @Override
    public List<AccountingDto.Game> gameAccount(AccountingDto.Command command) {
        List<AccountingDto.Game> list = new ArrayList<>();

        // 실시간
        for (MenuCode menuCode : MenuCode.getZoneList()) {
            command.setMenuCode(menuCode.toString());
            list.add(accountingMapper.gameAccount(command));
        }

        return list;
    }

    @Override
    public AccountingDto.Game gameInplay(AccountingDto.Command command) {
        return accountingMapper.gameInplay(command);
    }

    @Override
    public List<AccountingDto.Amount> money(AccountingDto.Command command) {
        return accountingMapper.money(command);
    }

    @Override
    public List<AccountingDto.Amount> point(AccountingDto.Command command) {
        return accountingMapper.point(command);
    }

    @Override
    public List<AccountingDto.Amount> board(AccountingDto.Command command) {
        return accountingMapper.board(command);
    }

    @Override
    public List<AccountingDto.Amount> comment(AccountingDto.Command command) {
        return accountingMapper.comment(command);
    }

    @Override
    public MonitorDto.Amount amount() {
        return monitorMapper.getAmount();
    }

    @Override
    public MonitorDto.Amount amount(AccountingDto.Command command) {
        return accountingMapper.amount(command);
    }

    @Override
    public Long fees(AccountingDto.Command command) {
        return accountingMapper.fees(command);
    }
}
