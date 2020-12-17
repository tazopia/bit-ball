package spoon.gameZone.lowhi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.lowhi.Lowhi;
import spoon.gameZone.lowhi.LowhiRepository;
import spoon.gameZone.lowhi.QLowhi;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LowhiBotServiceImpl implements LowhiBotService {

    private LowhiGameService lowhiGameService;

    private LowhiRepository lowhiRepository;

    private static QLowhi q = QLowhi.lowhi1;

    @Override
    public boolean notExist(Date gameDate) {
        return lowhiRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Lowhi lowhi) {
        lowhiRepository.saveAndFlush(lowhi);
    }

    @Transactional
    @Override
    public boolean closingGame(Lowhi result) {
        Lowhi lowhi = lowhiRepository.findOne(q.sdate.eq(result.getSdate()));
        if (lowhi == null) {
            return true;
        }

        try {
            lowhi.setOddeven(result.getOddeven());
            lowhi.setLowhi(result.getLowhi());
            lowhi.setClosing(true);

            lowhiRepository.saveAndFlush(lowhi);
            lowhiGameService.closingBetting(lowhi);
        } catch (RuntimeException e) {
            log.error("로하이 {}회차 결과 업데이트에 실패하였습니다. - {}", lowhi.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = lowhiRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getLowhi().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        lowhiRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
