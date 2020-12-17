package spoon.gameZone.crownOddeven.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.crownOddeven.CrownOddeven;
import spoon.gameZone.crownOddeven.CrownOddevenRepository;
import spoon.gameZone.crownOddeven.QCrownOddeven;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownOddevenBotServiceImpl implements CrownOddevenBotService {

    private CrownOddevenGameService crownOddevenGameService;

    private CrownOddevenRepository crownOddevenRepository;

    private static QCrownOddeven q = QCrownOddeven.crownOddeven;

    @Override
    public boolean notExist(Date gameDate) {
        return crownOddevenRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(CrownOddeven crownOddeven) {
        crownOddevenRepository.saveAndFlush(crownOddeven);
    }

    @Transactional
    @Override
    public boolean closingGame(CrownOddeven result) {
        CrownOddeven crownOddeven = crownOddevenRepository.findOne(q.sdate.eq(result.getSdate()));
        if (crownOddeven == null) {
            return true;
        }

        try {
            crownOddeven.setCard1(result.getCard1());
            crownOddeven.setCard2(result.getCard2());
            crownOddeven.setOddeven(result.getOddeven());

            if ("odd".equals(result.getOddeven()) || "even".equals(result.getOddeven())) {
                int hidden = Integer.parseInt(result.getCard1());
                crownOddeven.setOverunder(hidden > 5 ? "OVER" : "UNDER");
                crownOddeven.setCancel(false);
            } else {
                crownOddeven.setOverunder("");
                crownOddeven.setCancel(true);
            }
            crownOddeven.setClosing(true);

            crownOddevenRepository.saveAndFlush(crownOddeven);
            crownOddevenGameService.closingBetting(crownOddeven);
        } catch (RuntimeException e) {
            log.error("크라운 홀짝 결과 업데이트에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = crownOddevenRepository.count(q.gameDate.before(DateUtils.beforeSeconds(90)).and(q.closing.isFalse()));
        ZoneConfig.getOddeven().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        crownOddevenRepository.deleteGame(DateUtils.beforeDays(days));
    }

}
