package spoon.ip.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.ip.domain.IpAddrDto;
import spoon.ip.entity.IpAddr;
import spoon.ip.entity.QIpAddr;
import spoon.ip.repository.IpAddrRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class IpAddrServiceImpl implements IpAddrService {

    private IpAddrRepository ipAddrRepository;

    private static QIpAddr q = QIpAddr.ipAddr;

    @Override
    public Page<IpAddr> page(IpAddrDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.code.eq(command.getCode()));
        if (StringUtils.notEmpty(command.getIp())) {
            builder.and(q.ip.like(command.getIp() + "%"));
        }

        return ipAddrRepository.findAll(builder, pageable);
    }

    @Transactional
    @Override
    public AjaxResult add(IpAddrDto.Add add) {
        IpAddr addr = new IpAddr();
        addr.setIp(add.getIp());
        addr.setCode(add.getCode());
        addr.setMemo(add.getMemo());

        ipAddrRepository.saveAndFlush(addr);

        return new AjaxResult(true, "아이피 등록을 완료하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        ipAddrRepository.delete(id);
        return new AjaxResult(true, "아이피를 삭제하였습니다.");
    }

    @Override
    public boolean checkIp(String code) {
        String ip = WebUtils.ip();
        // 로컬에서 접속하는것은 허용한다.
        if ("localhost".equals(ip) || "127.0.0.1".equals(ip)) {
            return "admin".equals(code);
        }

        String ipGroup = ip.substring(0, ip.lastIndexOf(".") + 1) + "*";
        long count = ipAddrRepository.count(q.code.eq(code).and(q.ip.eq(ip).or(q.ip.eq(ipGroup))));
        return count > 0;
    }
}
