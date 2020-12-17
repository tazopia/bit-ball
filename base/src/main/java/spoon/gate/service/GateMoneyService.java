package spoon.gate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gate.domain.GateDto;
import spoon.payment.entity.Money;

public interface GateMoneyService {

    Page<Money> findAll(GateDto.Command command, Pageable pageable);

}
