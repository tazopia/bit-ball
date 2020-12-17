package spoon.gameZone.dari.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.dari.Dari;
import spoon.gameZone.dari.DariRepository;
import spoon.gameZone.dari.QDari;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class DariBotServiceImpl implements DariBotService {

    private DariGameService dariGameService;

    private DariRepository dariRepository;

    private static QDari q = QDari.dari;

    @Override
    public boolean notExist(Date gameDate) {
        return dariRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Dari dari) {
        dariRepository.saveAndFlush(dari);
    }

    @Transactional
    @Override
    public boolean closingGame(Dari result) {
        Dari dari = dariRepository.findOne(q.sdate.eq(result.getSdate()));
        if (dari == null) {
            return true;
        }

        try {
            dari.setOddeven(result.getOddeven());
            dari.setLine(result.getLine());
            dari.setStart(result.getStart());
            dari.setClosing(true);

            dariRepository.saveAndFlush(dari);
            dariGameService.closingBetting(dari);
        } catch (RuntimeException e) {
            log.error("다리다리 {}회차 결과 업데이트에 실패하였습니다. - {}", dari.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = dariRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getDari().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        dariRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
