package spoon.sale.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.accounting.domain.AccountingDto;
import spoon.game.domain.MenuCode;
import spoon.mapper.AgencyMapper;
import spoon.monitor.domain.MonitorDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AgencyService {

    private final AgencyMapper agencyMapper;

    public List<AccountingDto.Daily> daily(AccountingDto.Agency command) {
        return agencyMapper.daily(command);
    }


    public List<AccountingDto.Game> gameAccount(AccountingDto.Agency command) {
        List<AccountingDto.Game> list = new ArrayList<>();
        // 실시간
        for (MenuCode menuCode : MenuCode.getZoneList()) {
            command.setMenuCode(menuCode.toString());
            list.add(agencyMapper.gameAccount(command));
        }

        return list;
    }

    public AccountingDto.Game gameInplay(AccountingDto.Agency command) {
        return agencyMapper.gameInplay(command);
    }

    public MonitorDto.Amount amount(AccountingDto.Agency command) {
        return agencyMapper.amount(command);
    }

    public List<AccountingDto.Amount> money(AccountingDto.Agency command) {
        return agencyMapper.money(command);
    }

    public List<AccountingDto.Amount> point(AccountingDto.Agency command) {
        return agencyMapper.point(command);
    }
}
