package spoon.gameZone.snail.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.snail.QSnail;
import spoon.gameZone.snail.Snail;
import spoon.gameZone.snail.SnailRepository;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class SnailBotServiceImpl implements SnailBotService {

    private SnailGameService snailGameService;

    private SnailRepository snailRepository;

    private static QSnail q = QSnail.snail;

    @Override
    public boolean notExist(Date gameDate) {
        return snailRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Snail snail) {
        snailRepository.saveAndFlush(snail);
    }

    @Transactional
    @Override
    public boolean closingGame(Snail result) {
        Snail snail = snailRepository.findOne(q.sdate.eq(result.getSdate()));
        if (snail == null) {
            return true;
        }

        try {
            snail.setResult(result.getResult());
            snail.setClosing(true);

            snailRepository.saveAndFlush(snail);
            snailGameService.closingBetting(snail);
        } catch (RuntimeException e) {
            log.error("사다리 {}회차 결과 업데이트에 실패하였습니다. - {}", snail.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = snailRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getSnail().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        snailRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
