package spoon.gameZone.bitcoin3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Json;
import spoon.gameZone.bitcoin3.entity.Bitcoin3;
import spoon.gameZone.bitcoin3.entity.QBitcoin3;
import spoon.gameZone.bitcoin3.repository.Bitcoin3Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin3BotService {

    private final Bitcoin3Repository bitcoin3Repository;

    private final Bitcoin3GameService bitcoin3GameService;

    private static final QBitcoin3 q = QBitcoin3.bitcoin3;

    public boolean exists(LocalDateTime gameDate) {
        return bitcoin3Repository.exists(q.sdate.eq(gameDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))));
    }

    @Transactional
    public void add(Bitcoin3 entity) {
        bitcoin3Repository.save(entity);
    }

    @Transactional
    public boolean closingGame(Bitcoin3Json result) {
        Bitcoin3 entity = bitcoin3Repository.findOne(q.sdate.eq(result.getSdate()));
        if (entity == null) {
            return true;
        }

        try {
            entity.updateScore(result);
            bitcoin3GameService.closingBetting(entity);
        } catch (RuntimeException e) {
            log.error("비트코인 3분 {}회차 결과 업데이트에 실패하였습니다.", entity.getSdate(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = bitcoin3Repository.count(q.gameDate.before(DateUtils.beforeSeconds(150)).and(q.closing.isFalse()));
        ZoneConfig.getBitcoin3().setResult(cnt);
    }

    @Transactional
    public void deleteGame(Date beforeDays) {
        bitcoin3Repository.deleteGame(beforeDays);
    }
}
