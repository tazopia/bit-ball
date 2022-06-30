package spoon.gameZone.eos1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos1.entity.Eos1;
import spoon.gameZone.eos1.entity.QEos1;
import spoon.gameZone.eos1.repository.Eos1Repository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos1BotService {

    private final Eos1GameService eos1GameService;

    private final Eos1Repository eos1Repository;

    private static final QEos1 q = QEos1.eos1;

    public boolean notExist(Date gameDate) {
        return eos1Repository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Eos1 eos1) {
        eos1Repository.saveAndFlush(eos1);
    }

    @Transactional
    public boolean closingGame(Eos1 result) {
        Eos1 eos1 = eos1Repository.findOne(q.sdate.eq(result.getSdate()));
        if (eos1 == null) {
            return true;
        }

        try {
            eos1.setOddeven(result.getOddeven());
            eos1.setPb_oddeven(result.getPb_oddeven());
            eos1.setOverunder(result.getOverunder());
            eos1.setPb_overunder(result.getPb_overunder());
            eos1.setSize(result.getSize());
            eos1.setPb(result.getPb());
            eos1.setBall(result.getBall());
            eos1.setSize(result.getSize());
            eos1.setSum(result.getSum());

            eos1.setClosing(true);

            eos1Repository.saveAndFlush(eos1);
            eos1GameService.closingBetting(eos1);
        } catch (RuntimeException e) {
            log.error("Eos 1분 파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", eos1.getRound(), e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = eos1Repository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getEos1().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        eos1Repository.deleteGame(DateUtils.beforeDays(days));
    }
}
