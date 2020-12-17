package spoon.bot.balance.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spoon.bot.balance.entity.PolygonBalance;
import spoon.bot.balance.repository.PolygonBalanceRepository;

@Slf4j
@AllArgsConstructor
@Service
public class BalanceServiceImpl implements BalanceService {

    private PolygonBalanceRepository polygonBalanceRepository;

    @Override
    public Page<PolygonBalance> pagePolygon(Pageable pageable) {
        return polygonBalanceRepository.findAll(pageable);
    }
}
