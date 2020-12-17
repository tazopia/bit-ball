package spoon.gameZone.bitcoin5.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Json;
import spoon.gameZone.bitcoin5.entity.Bitcoin5;
import spoon.gameZone.bitcoin5.entity.QBitcoin5;
import spoon.gameZone.bitcoin5.repository.Bitcoin5Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin5BotService {

    private final Bitcoin5Repository bitcoin5Repository;

    private final Bitcoin5GameService bitcoin5GameService;

    private static final QBitcoin5 q = QBitcoin5.bitcoin5;

    public boolean exists(LocalDateTime gameDate) {
        return bitcoin5Repository.exists(q.sdate.eq(gameDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))));
    }

    @Transactional
    public void add(Bitcoin5 entity) {
        bitcoin5Repository.save(entity);
    }

    @Transactional
    public boolean closingGame(Bitcoin5Json result) {
        Bitcoin5 entity = bitcoin5Repository.findOne(q.sdate.eq(result.getSdate()));
        if (entity == null) {
            return true;
        }

        try {
            entity.updateScore(result);
            bitcoin5GameService.closingBetting(entity);
        } catch (RuntimeException e) {
            log.error("비트코인 5분 {}회차 결과 업데이트에 실패하였습니다.", entity.getSdate(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = bitcoin5Repository.count(q.gameDate.before(DateUtils.beforeSeconds(150)).and(q.closing.isFalse()));
        ZoneConfig.getBitcoin5().setResult(cnt);
    }

    @Transactional
    public void deleteGame(Date beforeDays) {
        bitcoin5Repository.deleteGame(beforeDays);
    }
}
