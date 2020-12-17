package spoon.gameZone.newSnail.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.newSnail.NewSnail;
import spoon.gameZone.newSnail.NewSnailRepository;
import spoon.gameZone.newSnail.QNewSnail;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class NewSnailBotServiceImpl implements NewSnailBotService {

    private NewSnailGameService newSnailGameService;

    private NewSnailRepository newSnailRepository;

    private static QNewSnail q = QNewSnail.newSnail;

    @Override
    public boolean notExist(Date gameDate) {
        return newSnailRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(NewSnail newSnail) {
        newSnailRepository.saveAndFlush(newSnail);
    }

    @Transactional
    @Override
    public boolean closingGame(NewSnail result) {
        NewSnail newSnail = newSnailRepository.findOne(q.sdate.eq(result.getSdate()));
        if (newSnail == null) {
            return true;
        }

        try {
            newSnail.setOe(result.getOe());
            newSnail.setOu(result.getOu());
            newSnail.setRanking(result.getRanking());
            newSnail.setClosing(true);

            newSnailRepository.saveAndFlush(newSnail);
            newSnailGameService.closingBetting(newSnail);
        } catch (RuntimeException e) {
            log.error("NEW 달팽이 {}회차 결과 업데이트에 실패하였습니다. - {}", newSnail.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = newSnailRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getNewSnail().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        newSnailRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
