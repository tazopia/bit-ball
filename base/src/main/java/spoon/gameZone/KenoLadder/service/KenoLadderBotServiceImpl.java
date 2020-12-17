package spoon.gameZone.KenoLadder.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.KenoLadder.KenoLadder;
import spoon.gameZone.KenoLadder.KenoLadderRepository;
import spoon.gameZone.KenoLadder.QKenoLadder;
import spoon.gameZone.ZoneConfig;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class KenoLadderBotServiceImpl implements KenoLadderBotService {

    private KenoLadderGameService kenoLadderGameService;

    private KenoLadderRepository kenoLadderRepository;

    private static QKenoLadder q = QKenoLadder.kenoLadder;

    @Override
    public boolean notExist(Date gameDate) {
        return kenoLadderRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(KenoLadder kenoLadder) {
        kenoLadderRepository.saveAndFlush(kenoLadder);
    }

    @Transactional
    @Override
    public boolean closingGame(KenoLadder result) {
        KenoLadder kenoLadder = kenoLadderRepository.findOne(q.sdate.eq(result.getSdate()));
        if (kenoLadder == null) {
            return true;
        }

        try {
            kenoLadder.setOddeven(result.getOddeven());
            kenoLadder.setLine(result.getLine());
            kenoLadder.setStart(result.getStart());
            kenoLadder.setClosing(true);

            kenoLadderRepository.saveAndFlush(kenoLadder);
            kenoLadderGameService.closingBetting(kenoLadder);
        } catch (RuntimeException e) {
            log.error("키노사다리 {}회차 결과 업데이트에 실패하였습니다. - {}", kenoLadder.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = kenoLadderRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getKenoLadder().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        kenoLadderRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
