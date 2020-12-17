package spoon.game.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.mapper.GameMapper;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class GameDeleteServiceImpl implements GameDeleteService {

    private GameMapper gameMapper;

    @Transactional
    @Override
    public void delete(int days) {
        Date gameDate = DateUtils.beforeDays(days);
        try {
            log.warn("--------------------------------------------------------------------");
            log.warn("{}일 지난 경기를 삭제합니다.", days);
            gameMapper.deleteBeforeGameDate(gameDate);
            log.warn("--------------------------------------------------------------------");
        } catch (RuntimeException e) {
            log.error("지난 경기를 삭제하지 못하였습니다. {}일 - {}", days, e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }
}
