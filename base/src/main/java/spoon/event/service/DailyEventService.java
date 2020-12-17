package spoon.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.config.domain.EventConfig;
import spoon.event.domain.DailyCalendar;
import spoon.event.domain.DailyDto;
import spoon.event.entity.DailyPayment;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface DailyEventService {

    Page<DailyPayment> paymentPage(DailyDto.Command command, Pageable pageable);

    boolean updateConfig(EventConfig eventConfig);

    AjaxResult payment(Long id);

    AjaxResult cancel(Long id);

    void checkEvent(String userid);

    List<DailyCalendar> calendar(String month);

    Iterable<DailyPayment> dailyPayment();

    int week(String month);

    String prev(String month);

    String next(String month);

    AjaxResult enabled(Long id);

    AjaxResult deleted(Long id);
}
