package spoon.gameZone.crownSutda.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.crownSutda.CrownSutda;
import spoon.gameZone.crownSutda.CrownSutdaRepository;
import spoon.gameZone.crownSutda.QCrownSutda;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownSutdaBotServiceImpl implements CrownSutdaBotService {

    private CrownSutdaGameService crownSutdaGameService;

    private CrownSutdaRepository crownSutdaRepository;

    private static QCrownSutda q = QCrownSutda.crownSutda;

    @Override
    public boolean notExist(Date gameDate) {
        return crownSutdaRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(CrownSutda crownSutda) {
        crownSutdaRepository.saveAndFlush(crownSutda);
    }

    @Transactional
    @Override
    public boolean closingGame(CrownSutda result) {
        CrownSutda crownSutda = crownSutdaRepository.findOne(q.sdate.eq(result.getSdate()));
        if (crownSutda == null) {
            return true;
        }

        try {
            crownSutda.setKorea(result.getKorea());
            crownSutda.setJapan(result.getJapan());
            crownSutda.setK1(result.getK1());
            crownSutda.setK2(result.getK2());
            crownSutda.setJ1(result.getJ1());
            crownSutda.setJ2(result.getJ2());
            crownSutda.setResult(result.getResult());

            if ("C".equals(result.getResult())) {
                crownSutda.setCancel(true);
            } else {
                crownSutda.setCancel(false);
            }
            crownSutda.setClosing(true);

            crownSutdaRepository.saveAndFlush(crownSutda);
            crownSutdaGameService.closingBetting(crownSutda);
        } catch (RuntimeException e) {
            log.error("섰다 결과 업데이트에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = crownSutdaRepository.count(q.gameDate.before(DateUtils.beforeSeconds(100)).and(q.closing.isFalse()));
        ZoneConfig.getCrownSutda().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        crownSutdaRepository.deleteGame(DateUtils.beforeDays(days));
    }

}
