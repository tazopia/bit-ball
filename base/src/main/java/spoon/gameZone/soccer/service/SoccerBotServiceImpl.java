package spoon.gameZone.soccer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.soccer.QSoccer;
import spoon.gameZone.soccer.Soccer;
import spoon.gameZone.soccer.SoccerDto;
import spoon.gameZone.soccer.SoccerRepository;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class SoccerBotServiceImpl implements SoccerBotService {

    private SoccerGameService soccerGameService;

    private SoccerRepository soccerRepository;

    private static QSoccer q = QSoccer.soccer;

    @Override
    public boolean notExist(Date gameDate) {
        return soccerRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Soccer soccer) {
        soccerRepository.saveAndFlush(soccer);
    }

    @Transactional
    @Override
    public boolean closingGame(Soccer result) {
        Soccer soccer = soccerRepository.findOne(q.sdate.eq(result.getSdate()));
        if (soccer == null) {
            return true;
        }

        try {
            SoccerDto.Score score = new SoccerDto.Score();
            score.setScoreHome(result.getScoreHome());
            score.setScoreAway(result.getScoreAway());
            soccer.updateScore(score);

            soccerRepository.saveAndFlush(soccer);
            soccerGameService.closingBetting(soccer);
        } catch (RuntimeException e) {
            log.error("가상축구 {}회차 결과 업데이트에 실패하였습니다. - {}", soccer.getSdate(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = soccerRepository.count(q.gameDate.before(DateUtils.beforeMinutes(3)).and(q.closing.isFalse()));
        ZoneConfig.getSoccer().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        soccerRepository.deleteGame(DateUtils.beforeDays(days));
    }

    @Override
    public String getLastGame(String edate) {
        return soccerRepository.getLastSdate(edate);
    }
}
