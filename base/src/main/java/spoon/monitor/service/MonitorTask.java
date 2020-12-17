package spoon.monitor.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class MonitorTask {

    private MonitorService monitorService;

    @Scheduled(fixedRate = 13 * 1000)
    public void parsingGame() {
        monitorService.checkAmount();
    }

}
