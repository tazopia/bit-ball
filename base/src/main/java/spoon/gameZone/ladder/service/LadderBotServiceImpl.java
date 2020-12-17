package spoon.gameZone.ladder.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ladder.Ladder;
import spoon.gameZone.ladder.LadderRepository;
import spoon.gameZone.ladder.QLadder;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LadderBotServiceImpl implements LadderBotService {

    private LadderGameService ladderGameService;

    private LadderRepository ladderRepository;

    private static QLadder q = QLadder.ladder;

    @Override
    public boolean notExist(Date gameDate) {
        return ladderRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Ladder ladder) {
        ladderRepository.saveAndFlush(ladder);
    }

    @Transactional
    @Override
    public boolean closingGame(Ladder result) {
        Ladder ladder = ladderRepository.findOne(q.sdate.eq(result.getSdate()));
        if (ladder == null) {
            return true;
        }

        try {
            ladder.setOddeven(result.getOddeven());
            ladder.setLine(result.getLine());
            ladder.setStart(result.getStart());
            ladder.setClosing(true);

            ladderRepository.saveAndFlush(ladder);
            ladderGameService.closingBetting(ladder);
        } catch (RuntimeException e) {
            log.error("사다리 {}회차 결과 업데이트에 실패하였습니다. - {}", ladder.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = ladderRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getLadder().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        ladderRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
