package spoon.ip.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.ip.domain.IpAddrDto;
import spoon.ip.entity.IpAddr;
import spoon.support.web.AjaxResult;

public interface IpAddrService {

    Page<IpAddr> page(IpAddrDto.Command command, Pageable pageable);

    AjaxResult add(IpAddrDto.Add add);

    AjaxResult delete(Long id);

    boolean checkIp(String code);
}
