package spoon.gameZone.eos2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos2.entity.Eos2;
import spoon.gameZone.eos2.entity.QEos2;
import spoon.gameZone.eos2.repository.Eos2Repository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos2BotService {

    private final Eos2GameService eos2GameService;

    private final Eos2Repository eos2Repository;

    private static final QEos2 q = QEos2.eos2;

    public boolean notExist(Date gameDate) {
        return eos2Repository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Eos2 eos2) {
        eos2Repository.saveAndFlush(eos2);
    }

    @Transactional
    public boolean closingGame(Eos2 result) {
        Eos2 eos2 = eos2Repository.findOne(q.sdate.eq(result.getSdate()));
        if (eos2 == null) {
            return true;
        }

        try {
            eos2.setOddeven(result.getOddeven());
            eos2.setPb_oddeven(result.getPb_oddeven());
            eos2.setOverunder(result.getOverunder());
            eos2.setPb_overunder(result.getPb_overunder());
            eos2.setSize(result.getSize());
            eos2.setPb(result.getPb());
            eos2.setBall(result.getBall());
            eos2.setSize(result.getSize());
            eos2.setSum(result.getSum());

            eos2.setClosing(true);

            eos2Repository.saveAndFlush(eos2);
            eos2GameService.closingBetting(eos2);
        } catch (RuntimeException e) {
            log.error("Eos 2분 파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", eos2.getRound(), e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = eos2Repository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getEos2().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        eos2Repository.deleteGame(DateUtils.beforeDays(days));
    }
}
