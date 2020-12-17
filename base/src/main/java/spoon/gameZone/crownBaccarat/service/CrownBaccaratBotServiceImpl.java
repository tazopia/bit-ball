package spoon.gameZone.crownBaccarat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.crownBaccarat.CrownBaccarat;
import spoon.gameZone.crownBaccarat.CrownBaccaratRepository;
import spoon.gameZone.crownBaccarat.QCrownBaccarat;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownBaccaratBotServiceImpl implements CrownBaccaratBotService {

    private CrownBaccaratGameService crownBaccaratGameService;

    private CrownBaccaratRepository crownBaccaratRepository;

    private static QCrownBaccarat q = QCrownBaccarat.crownBaccarat;

    @Override
    public boolean notExist(Date gameDate) {
        return crownBaccaratRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(CrownBaccarat crownBaccarat) {
        crownBaccaratRepository.saveAndFlush(crownBaccarat);
    }

    @Transactional
    @Override
    public boolean closingGame(CrownBaccarat result) {
        CrownBaccarat crownBaccarat = crownBaccaratRepository.findOne(q.sdate.eq(result.getSdate()));
        if (crownBaccarat == null) {
            return true;
        }

        try {
            crownBaccarat.setP(result.getP());
            crownBaccarat.setB(result.getB());
            crownBaccarat.setC(result.getC());
            crownBaccarat.setResult(result.getResult());

            if ("C".equals(result.getResult())) {
                crownBaccarat.setCancel(true);
            } else {
                crownBaccarat.setCancel(false);
            }
            crownBaccarat.setClosing(true);

            crownBaccaratRepository.saveAndFlush(crownBaccarat);
            crownBaccaratGameService.closingBetting(crownBaccarat);
        } catch (RuntimeException e) {
            log.error("바카라 결과 업데이트에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = crownBaccaratRepository.count(q.gameDate.before(DateUtils.beforeSeconds(100)).and(q.closing.isFalse()));
        ZoneConfig.getCrownBaccarat().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        crownBaccaratRepository.deleteGame(DateUtils.beforeDays(days));
    }

}
