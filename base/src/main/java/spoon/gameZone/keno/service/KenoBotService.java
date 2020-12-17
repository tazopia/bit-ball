package spoon.gameZone.keno.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.keno.entity.Keno;
import spoon.gameZone.keno.entity.QKeno;
import spoon.gameZone.keno.repository.KenoRepository;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class KenoBotService {

    private final KenoGameService kenoGameService;

    private final KenoRepository kenoRepository;

    private static final QKeno q = QKeno.keno;

    public boolean notExist(Date gameDate) {
        return kenoRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    public void addGame(Keno keno) {
        kenoRepository.saveAndFlush(keno);
    }

    @Transactional
    public boolean closingGame(Keno result) {
        Keno keno = kenoRepository.findOne(q.sdate.eq(result.getSdate()));
        if (keno == null) {
            return true;
        }

        try {
            keno.setOddeven(result.getOddeven());
            keno.setOverunder(result.getOverunder());
            keno.setSum(result.getSum());
            keno.setClosing(true);

            kenoRepository.saveAndFlush(keno);
            kenoGameService.closingBetting(keno);
        } catch (RuntimeException e) {
            log.error("스피드키노 {}회차 결과 업데이트에 실패하였습니다.", keno.getRound(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = kenoRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getKeno().setResult(cnt);
    }

    @Transactional
    public void deleteGame(int days) {
        kenoRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
