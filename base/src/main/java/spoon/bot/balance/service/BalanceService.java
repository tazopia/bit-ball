package spoon.bot.balance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.bot.balance.entity.PolygonBalance;

public interface BalanceService {

    Page<PolygonBalance> pagePolygon(Pageable pageable);
}
