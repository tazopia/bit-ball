package spoon.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.config.domain.EventLottoConfig;
import spoon.event.domain.LottoDto;
import spoon.event.entity.LottoPayment;
import spoon.support.web.AjaxResult;

public interface LottoEventService {

    boolean updateConfig(EventLottoConfig eventLottoConfig);

    AjaxResult lotto();

    Page<LottoPayment> paymentPage(LottoDto.Command command, Pageable pageable);
}
