package spoon.gameZone.eos3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos3.entity.Eos3;
import spoon.gameZone.eos3.entity.QEos3;
import spoon.gameZone.eos3.repository.Eos3Repository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos3BotService {

    private final Eos3GameService eos3GameService;

    private final Eos3Repository eos3Repository;

    private static final QEos3 q = QEos3.eos3;

    public boolean notExist(Date gameDate) {
        return eos3Repository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Eos3 eos3) {
        eos3Repository.saveAndFlush(eos3);
    }

    @Transactional
    public boolean closingGame(Eos3 result) {
        Eos3 eos3 = eos3Repository.findOne(q.sdate.eq(result.getSdate()));
        if (eos3 == null) {
            return true;
        }

        try {
            eos3.setOddeven(result.getOddeven());
            eos3.setPb_oddeven(result.getPb_oddeven());
            eos3.setOverunder(result.getOverunder());
            eos3.setPb_overunder(result.getPb_overunder());
            eos3.setSize(result.getSize());
            eos3.setPb(result.getPb());
            eos3.setBall(result.getBall());
            eos3.setSize(result.getSize());
            eos3.setSum(result.getSum());

            eos3.setClosing(true);

            eos3Repository.saveAndFlush(eos3);
            eos3GameService.closingBetting(eos3);
        } catch (RuntimeException e) {
            log.error("Eos 3분 파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", eos3.getRound(), e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = eos3Repository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getEos3().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        eos3Repository.deleteGame(DateUtils.beforeDays(days));
    }
}
