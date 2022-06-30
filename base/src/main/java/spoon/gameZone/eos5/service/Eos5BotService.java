package spoon.gameZone.eos5.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos5.entity.Eos5;
import spoon.gameZone.eos5.entity.QEos5;
import spoon.gameZone.eos5.repository.Eos5Repository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos5BotService {

    private final Eos5GameService eos5GameService;

    private final Eos5Repository eos5Repository;

    private static final QEos5 q = QEos5.eos5;

    public boolean notExist(Date gameDate) {
        return eos5Repository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Eos5 eos5) {
        eos5Repository.saveAndFlush(eos5);
    }

    @Transactional
    public boolean closingGame(Eos5 result) {
        Eos5 eos5 = eos5Repository.findOne(q.sdate.eq(result.getSdate()));
        if (eos5 == null) {
            return true;
        }

        try {
            eos5.setOddeven(result.getOddeven());
            eos5.setPb_oddeven(result.getPb_oddeven());
            eos5.setOverunder(result.getOverunder());
            eos5.setPb_overunder(result.getPb_overunder());
            eos5.setSize(result.getSize());
            eos5.setPb(result.getPb());
            eos5.setBall(result.getBall());
            eos5.setSize(result.getSize());
            eos5.setSum(result.getSum());

            eos5.setClosing(true);

            eos5Repository.saveAndFlush(eos5);
            eos5GameService.closingBetting(eos5);
        } catch (RuntimeException e) {
            log.error("Eos 5분 파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", eos5.getRound(), e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = eos5Repository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getEos5().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        eos5Repository.deleteGame(DateUtils.beforeDays(days));
    }
}
