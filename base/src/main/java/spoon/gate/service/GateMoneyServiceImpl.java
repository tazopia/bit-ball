package spoon.gate.service;


import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spoon.common.utils.DateUtils;
import spoon.gate.domain.GateDto;
import spoon.payment.domain.MoneyCode;
import spoon.payment.entity.Money;
import spoon.payment.entity.QMoney;
import spoon.payment.repository.MoneyRepository;

@AllArgsConstructor
@Service
public class GateMoneyServiceImpl implements GateMoneyService {

    private MoneyRepository moneyRepository;

    @Override
    public Page<Money> findAll(GateDto.Command command, Pageable pageable) {
        QMoney q = QMoney.money;

        BooleanBuilder builder = new BooleanBuilder(q.moneyCode.in(MoneyCode.GATE_IN, MoneyCode.GATE_OUT));
        builder.and(q.regDate.goe(DateUtils.start(command.getSdate()))).and(q.regDate.lt(DateUtils.end(command.getEdate())));

        return moneyRepository.findAll(builder, pageable);
    }
}
