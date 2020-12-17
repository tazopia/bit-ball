package spoon.gate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gate.domain.GateConfig;
import spoon.gate.domain.GateDto;
import spoon.payment.entity.Money;
import spoon.support.web.AjaxResult;

public interface GateService {

    boolean updateGateConfig(GateConfig config);

    String login();

    String getLiveUrl();

    String getSudaUrl();

    String getGraphUrl();

    long getGameMoney();

    AjaxResult exchange(long amount, int code);

    Page<Money> page(GateDto.Command command, Pageable pageable);

}
