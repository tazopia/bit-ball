package spoon.gameZone.luck.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.luck.Luck;
import spoon.gameZone.luck.LuckRepository;
import spoon.gameZone.luck.QLuck;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LuckBotServiceImpl implements LuckBotService {

    private LuckGameService luckGameService;

    private LuckRepository luckRepository;

    private static QLuck q = QLuck.luck;

    @Override
    public boolean notExist(Date gameDate) {
        return luckRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Luck luck) {
        luckRepository.saveAndFlush(luck);
    }

    @Transactional
    @Override
    public boolean closingGame(Luck result) {
        Luck luck = luckRepository.findOne(q.sdate.eq(result.getSdate()));
        if (luck == null) {
            return true;
        }

        try {
            luck.setDealer(result.getDealer());
            luck.setPlayer1(result.getPlayer1());
            luck.setPlayer2(result.getPlayer2());
            luck.setPlayer3(result.getPlayer3());
            luck.setResult(result.getResult());
            luck.setCancel(StringUtils.empty(result.getDealer()));
            luck.setClosing(true);

            luckRepository.saveAndFlush(luck);
            luckGameService.closingBetting(luck);
        } catch (RuntimeException e) {
            log.error("세븐럭 {}회차 결과 업데이트에 실패하였습니다. - {}", luck.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = luckRepository.count(q.gameDate.before(new Date()).and(q.closing.isFalse()));
        ZoneConfig.getLuck().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        luckRepository.deleteGame(DateUtils.beforeDays(days));
    }
}
