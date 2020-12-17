package spoon.gameZone.oddeven.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.oddeven.Oddeven;
import spoon.gameZone.oddeven.OddevenRepository;
import spoon.gameZone.oddeven.QOddeven;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class OddevenBotServiceImpl implements OddevenBotService {

    private OddevenGameService oddevenGameService;

    private OddevenRepository oddevenRepository;

    private static QOddeven q = QOddeven.oddeven1;

    @Override
    public boolean notExist(Date gameDate) {
        return oddevenRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Oddeven oddeven) {
        oddevenRepository.saveAndFlush(oddeven);
    }

    @Transactional
    @Override
    public boolean closingGame(Oddeven result) {
        Oddeven oddeven = oddevenRepository.findOne(q.sdate.eq(result.getSdate()));
        if (oddeven == null) {
            return true;
        }

        try {
            oddeven.setCard1(result.getCard1());
            oddeven.setCard2(result.getCard2());
            oddeven.setOddeven(result.getOddeven());

            if ("O".equals(result.getOddeven()) || "E".equals(result.getOddeven())) {
                String pattern = result.getCard1().substring(0, 1);
                int hidden = Integer.parseInt(result.getCard1().substring(1), 10);
                if (hidden == 14) hidden = 1;
                oddeven.setOverunder(hidden > 5 ? "OVER" : "UNDER");
                oddeven.setPattern(getPattern(pattern));
                oddeven.setCancel(false);
            } else {
                oddeven.setOverunder("");
                oddeven.setPattern("");
                oddeven.setCancel(true);
            }
            oddeven.setClosing(true);

            oddevenRepository.saveAndFlush(oddeven);
            oddevenGameService.closingBetting(oddeven);
        } catch (RuntimeException e) {
            log.error("홀짝 {}회차 결과 업데이트에 실패하였습니다. - {}", oddeven.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = oddevenRepository.count(q.gameDate.before(DateUtils.beforeSeconds(90)).and(q.closing.isFalse()));
        ZoneConfig.getOddeven().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        oddevenRepository.deleteGame(DateUtils.beforeDays(days));
    }

    private String getPattern(String p) {
        switch (p) {
            case "1":
                return "스페이드";
            case "2":
                return "하트";
            case "3":
                return "크로바";
            case "4":
                return "다이아";
            default:
                return "";
        }
    }
}
