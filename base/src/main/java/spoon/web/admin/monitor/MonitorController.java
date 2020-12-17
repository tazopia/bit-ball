package spoon.web.admin.monitor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.monitor.domain.Monitor;
import spoon.monitor.service.MonitorService;

@Slf4j
@AllArgsConstructor
@RestController("admin.monitorController")
@RequestMapping("#{config.pathAdmin}")
public class MonitorController {

    private MonitorService monitorService;

    @RequestMapping(value = "/api/monitor", method = RequestMethod.POST)
    public Monitor monitor() {
        return monitorService.getMonitor();
    }

}
