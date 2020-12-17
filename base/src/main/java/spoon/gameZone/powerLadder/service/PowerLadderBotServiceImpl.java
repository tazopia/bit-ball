package spoon.gameZone.powerLadder.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.powerLadder.PowerLadder;
import spoon.gameZone.powerLadder.PowerLadderRepository;
import spoon.gameZone.powerLadder.QPowerLadder;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class PowerLadderBotServiceImpl implements PowerLadderBotService {

    private PowerLadderGameService powerLadderGameService;

    private PowerLadderRepository powerLadderRepository;

    private static QPowerLadder q = QPowerLadder.powerLadder;

    @Override
    public boolean notExist(Date gameDate) {
        return powerLadderRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(PowerLadder powerLadder) {
        powerLadderRepository.saveAndFlush(powerLadder);
    }

    @Transactional
    @Override
    public boolean closingGame(PowerLadder result) {
        PowerLadder powerLadder = powerLadderRepository.findOne(q.sdate.eq(result.getSdate()));
        if (powerLadder == null) {
            return true;
        }

        try {
            powerLadder.setOddeven(result.getOddeven());
            powerLadder.setLine(result.getLine());
            powerLadder.setStart(result.getStart());
            powerLadder.setClosing(true);

            powerLadderRepository.saveAndFlush(powerLadder);
            powerLadderGameService.closingBetting(powerLadder);
        } catch (RuntimeException e) {
            log.error("파워사다리 {}회차 결과 업데이트에 실패하였습니다. - {}", powerLadder.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = powerLadderRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getPowerLadder().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        powerLadderRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
