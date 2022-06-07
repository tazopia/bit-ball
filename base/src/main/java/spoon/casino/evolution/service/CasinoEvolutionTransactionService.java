package spoon.casino.evolution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.casino.evolution.domain.CasinoEvolutionBetDto;
import spoon.casino.evolution.domain.CasinoEvolutionMyInfo;
import spoon.casino.evolution.repository.CasinoEvolutionDao;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.gameZone.ZoneConfig;
import spoon.monitor.service.MonitorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CasinoEvolutionTransactionService {

    private final CasinoEvolutionDao casinoEvolutionDao;

    private final CasinoEvolutionBetService casinoEvolutionBetService;

    private final MonitorService monitorService;

    private static final Map<String, String> headers = new HashMap<>();

    private static long TRANSACTION = 0;

    public void init() {
        TRANSACTION = casinoEvolutionDao.lastTransactionId();

        headers.put("Authorization", "Bearer " + ZoneConfig.getCasinoEvolution().getApiKey());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
    }

    public void getHistory() {
        if (TRANSACTION == 0) this.init();
        String startDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String json = HttpParsing.getCasinoEvolution(String.format(ZoneConfig.getCasinoEvolution().getTransaction(), startDate, TRANSACTION), headers);
        if (json == null) return;

        CasinoEvolutionBetDto bets = JsonUtils.toModel(json, CasinoEvolutionBetDto.class);
        if (bets == null) return;

        for (CasinoEvolutionBetDto.Bet bet : bets.getData()) {
            if ("bet".equals(bet.getType())) {
                casinoEvolutionBetService.bet(bet);
            } else if ("win".equals(bet.getType())) {
                casinoEvolutionBetService.win(bet);
            }

            if (TRANSACTION < bet.getId()) TRANSACTION = bet.getId();
        }
    }

    public void getAmount() {
        String json = HttpParsing.getCasinoEvolution(ZoneConfig.getCasinoEvolution().getMyInfo(), headers);
        if (json == null) return;

        CasinoEvolutionMyInfo info = JsonUtils.toModel(json, CasinoEvolutionMyInfo.class);
        if (info == null) return;

        monitorService.getMonitor().setCasinoBalance(Math.round(info.getBalance()));
    }

}
