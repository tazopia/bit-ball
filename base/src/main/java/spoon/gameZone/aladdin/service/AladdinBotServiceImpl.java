package spoon.gameZone.aladdin.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.aladdin.Aladdin;
import spoon.gameZone.aladdin.AladdinRepository;
import spoon.gameZone.aladdin.QAladdin;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class AladdinBotServiceImpl implements AladdinBotService {

    private AladdinGameService aladdinGameService;

    private AladdinRepository aladdinRepository;

    private static QAladdin q = QAladdin.aladdin;

    @Override
    public boolean notExist(Date gameDate) {
        return aladdinRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Aladdin aladdin) {
        aladdinRepository.saveAndFlush(aladdin);
    }

    @Transactional
    @Override
    public boolean closingGame(Aladdin result) {
        Aladdin aladdin = aladdinRepository.findOne(q.sdate.eq(result.getSdate()));
        if (aladdin == null) {
            return true;
        }

        try {
            aladdin.setOddeven(result.getOddeven());
            aladdin.setLine(result.getLine());
            aladdin.setStart(result.getStart());
            aladdin.setClosing(true);

            aladdinRepository.saveAndFlush(aladdin);
            aladdinGameService.closingBetting(aladdin);
        } catch (RuntimeException e) {
            log.error("알라딘 {}회차 결과 업데이트에 실패하였습니다. - {}", aladdin.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long count = aladdinRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getAladdin().setResult(count);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        aladdinRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
