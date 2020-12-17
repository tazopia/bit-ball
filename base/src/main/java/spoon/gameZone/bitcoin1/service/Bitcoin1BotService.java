package spoon.gameZone.bitcoin1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin1.domain.Bitcoin1Json;
import spoon.gameZone.bitcoin1.entity.Bitcoin1;
import spoon.gameZone.bitcoin1.entity.QBitcoin1;
import spoon.gameZone.bitcoin1.repository.Bitcoin1Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin1BotService {

    private final Bitcoin1Repository bitcoin1Repository;

    private final Bitcoin1GameService bitcoin1GameService;

    private static final QBitcoin1 q = QBitcoin1.bitcoin1;

    public boolean exists(LocalDateTime gameDate) {
        return bitcoin1Repository.exists(q.sdate.eq(gameDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))));
    }

    @Transactional
    public void add(Bitcoin1 entity) {
        bitcoin1Repository.save(entity);
    }

    @Transactional
    public boolean closingGame(Bitcoin1Json result) {
        Bitcoin1 entity = bitcoin1Repository.findOne(q.sdate.eq(result.getSdate()));
        if (entity == null) {
            return true;
        }

        try {
            entity.updateScore(result);
            bitcoin1GameService.closingBetting(entity);
        } catch (RuntimeException e) {
            log.error("비트코인 1분 {}회차 결과 업데이트에 실패하였습니다.", entity.getSdate(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public void checkResult() {
        long cnt = bitcoin1Repository.count(q.gameDate.before(DateUtils.beforeSeconds(100)).and(q.closing.isFalse()));
        ZoneConfig.getBitcoin1().setResult(cnt);
    }

    @Transactional
    public void deleteGame(Date beforeDays) {
        bitcoin1Repository.deleteGame(beforeDays);
    }
}
