package spoon.inPlay.config.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import spoon.common.utils.StringUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.domain.InPlayMarketDto;
import spoon.inPlay.config.entity.InPlayMarket;
import spoon.inPlay.config.entity.QInPlayMarket;
import spoon.inPlay.config.repository.InPlayMarketRepository;
import spoon.support.web.AjaxResult;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class InPlayMarketService {

    private final InPlayMarketRepository inPlayMarketRepository;

    private static final QInPlayMarket MARKET = QInPlayMarket.inPlayMarket;

    @PostConstruct
    public void init() {
        inPlayMarketRepository.findAll().forEach(x -> InPlayConfig.getMarket().put(x.getId(), x));
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addMarket(InPlayMarket market) {
        boolean exist = inPlayMarketRepository.exists(market.getId());
        if (exist) {
            if (!InPlayConfig.getMarket().containsKey(market.getId())) {
                market = inPlayMarketRepository.findOne(market.getId());
                InPlayConfig.getMarket().put(market.getId(), market);
            }
            return;
        }

        Integer sort = inPlayMarketRepository.getMaxSort();
        market.updateSort(sort == null ? 0 : sort);
        inPlayMarketRepository.save(market);
        InPlayConfig.getMarket().put(market.getId(), market);
    }

    public Iterable<InPlayMarket> getList(InPlayMarketDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getName())) {
            MARKET.name.contains(command.getName()).or(MARKET.korName.contains(command.getName()));
        }
        return inPlayMarketRepository.findAll(builder, new Sort(Sort.Direction.DESC, "enabled").and(new Sort("sort")));
    }

    public Iterable<InPlayMarket> getList() {
        return inPlayMarketRepository.findAll(MARKET.enabled.isTrue(), new Sort("sort"));
    }

    public Iterable<InPlayMarket> getAdminList() {
        return inPlayMarketRepository.findAll(new Sort(Sort.Direction.DESC, "enabled").and(new Sort("sort")));
    }

    public InPlayMarket findOne(Long id) {
        return inPlayMarketRepository.findOne(id);
    }

    @Transactional
    public void update(InPlayMarketDto.Update update) {
        InPlayMarket market = inPlayMarketRepository.findOne(update.getId());
        market.update(update);
    }

    // 업일때는 하나 sort가 하나 작은것을 찾아서 올려주고 내려준다.
    @Transactional
    public AjaxResult up(Long id) {
        InPlayMarket up = inPlayMarketRepository.findOne(id);
        InPlayMarket down = inPlayMarketRepository.findOne(MARKET.sort.eq(up.getSort() - 1));

        if (down == null) return new AjaxResult(false, "이미 최상위 입니다.");

        up.up();
        down.down();

        return new AjaxResult(true);
    }

    @Transactional
    public AjaxResult down(Long id) {
        InPlayMarket down = inPlayMarketRepository.findOne(id);
        InPlayMarket up = inPlayMarketRepository.findOne(MARKET.sort.eq(down.getSort() + 1));

        if (up == null) return new AjaxResult(false, "이미 마지막 입니다.");

        up.up();
        down.down();

        return new AjaxResult(true);
    }
}
