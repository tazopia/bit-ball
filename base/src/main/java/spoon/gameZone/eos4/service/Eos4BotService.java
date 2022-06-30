package spoon.gameZone.eos4.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos4.entity.Eos4;
import spoon.gameZone.eos4.entity.QEos4;
import spoon.gameZone.eos4.repository.Eos4Repository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos4BotService {

    private final Eos4GameService eos4GameService;

    private final Eos4Repository eos4Repository;

    private static final QEos4 q = QEos4.eos4;

    public boolean notExist(Date gameDate) {
        return eos4Repository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Eos4 eos4) {
        eos4Repository.saveAndFlush(eos4);
    }

    @Transactional
    public boolean closingGame(Eos4 result) {
        Eos4 eos4 = eos4Repository.findOne(q.sdate.eq(result.getSdate()));
        if (eos4 == null) {
            return true;
        }

        try {
            eos4.setOddeven(result.getOddeven());
            eos4.setPb_oddeven(result.getPb_oddeven());
            eos4.setOverunder(result.getOverunder());
            eos4.setPb_overunder(result.getPb_overunder());
            eos4.setSize(result.getSize());
            eos4.setPb(result.getPb());
            eos4.setBall(result.getBall());
            eos4.setSize(result.getSize());
            eos4.setSum(result.getSum());

            eos4.setClosing(true);

            eos4Repository.saveAndFlush(eos4);
            eos4GameService.closingBetting(eos4);
        } catch (RuntimeException e) {
            log.error("Eos 4분 파워볼 {}회차 결과 업데이트에 실패하였습니다. - {}", eos4.getRound(), e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = eos4Repository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getEos4().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        eos4Repository.deleteGame(DateUtils.beforeDays(days));
    }
}
