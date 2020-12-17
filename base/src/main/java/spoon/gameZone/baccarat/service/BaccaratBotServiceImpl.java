package spoon.gameZone.baccarat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.baccarat.Baccarat;
import spoon.gameZone.baccarat.BaccaratRepository;
import spoon.gameZone.baccarat.QBaccarat;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BaccaratBotServiceImpl implements BaccaratBotService {

    private BaccaratGameService baccaratGameService;

    private BaccaratRepository baccaratRepository;

    private static QBaccarat q = QBaccarat.baccarat;

    @Override
    public boolean notExist(Date gameDate) {
        return baccaratRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Baccarat baccarat) {
        baccaratRepository.saveAndFlush(baccarat);
    }

    @Transactional
    @Override
    public boolean closingGame(Baccarat result) {
        Baccarat baccarat = baccaratRepository.findOne(q.sdate.eq(result.getSdate()));
        if (baccarat == null) {
            return true;
        }

        try {
            baccarat.setP1(result.getP1());
            baccarat.setP2(result.getP2());
            baccarat.setP3(result.getP3());
            baccarat.setB1(result.getB1());
            baccarat.setB2(result.getB2());
            baccarat.setB3(result.getB3());
            baccarat.setResult(result.getResult());

            if (StringUtils.empty(result.getP1())) {
                baccarat.setCancel(true);
            } else {
                baccarat.setCancel(false);
            }
            baccarat.setClosing(true);

            baccaratRepository.saveAndFlush(baccarat);
            baccaratGameService.closingBetting(baccarat);
        } catch (RuntimeException e) {
            log.error("바카라 {}회차 결과 업데이트에 실패하였습니다. - {}", baccarat.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = baccaratRepository.count(q.gameDate.before(DateUtils.beforeSeconds(100)).and(q.closing.isFalse()));
        ZoneConfig.getBaccarat().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        baccaratRepository.deleteGame(DateUtils.beforeDays(days));
    }

}
