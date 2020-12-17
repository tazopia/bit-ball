package spoon.payment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.ErrorUtils;
import spoon.mapper.PaymentMapper;
import spoon.payment.entity.AddMoney;
import spoon.payment.entity.AddPoint;
import spoon.payment.repository.AddMoneyRepository;
import spoon.payment.repository.AddPointRepository;

@Slf4j
@AllArgsConstructor
@Service
public class AddPaymentServiceImpl implements AddPaymentService {

    private PaymentMapper paymentMapper;

    private AddMoneyRepository addMoneyRepository;

    private AddPointRepository addPointRepository;

    @Transactional
    @Override
    public void addMoney() {
        Iterable<AddMoney> addMonies = addMoneyRepository.findAll();
        for (AddMoney addMoney : addMonies) {
            try {
                paymentMapper.addMoney(addMoney);
            } catch (RuntimeException e) {
                log.error("머니 지급에 실패하였습니다. - {} - {}", e.getMessage(), addMoney.toString());
                log.info("{}", ErrorUtils.trace(e.getStackTrace()));
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            }
        }
    }

    @Transactional
    @Override
    public void addPoint() {
        Iterable<AddPoint> addPoints = addPointRepository.findAll();
        for (AddPoint addPoint : addPoints) {
            try {
                paymentMapper.addPoint(addPoint);
            } catch (RuntimeException e) {
                log.error("포인트 지급에 실패하였습니다. - {} - {}", e.getMessage(), addPoint.toString());
                log.info("{}", ErrorUtils.trace(e.getStackTrace()));
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            }
        }
    }

}
