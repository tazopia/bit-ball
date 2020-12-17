package spoon.gameZone.power.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.power.Power;
import spoon.gameZone.power.PowerRepository;
import spoon.gameZone.power.QPower;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class PowerBotServiceImpl implements PowerBotService {

    private PowerGameService powerGameService;

    private PowerRepository powerRepository;

    private static QPower q = QPower.power;

    @Override
    public boolean notExist(Date gameDate) {
        return powerRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Power power) {
        powerRepository.saveAndFlush(power);
    }

    @Transactional
    @Override
    public boolean closingGame(Power result) {
        Power power = powerRepository.findOne(q.sdate.eq(result.getSdate()));
        if (power == null) {
            return true;
        }

        try {
            power.setOddeven(result.getOddeven());
            power.setPb_oddeven(result.getPb_oddeven());
            power.setOverunder(result.getOverunder());
            power.setPb_overunder(result.getPb_overunder());
            power.setSize(result.getSize());
            power.setPb(result.getPb());
            power.setBall(result.getBall());
            power.setSize(result.getSize());
            power.setSum(result.getSum());

            power.setClosing(true);

            powerRepository.saveAndFlush(power);
            powerGameService.closingBetting(power);
        } catch (RuntimeException e) {
            log.error("파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", power.getTimes(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = powerRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getPower().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        powerRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
