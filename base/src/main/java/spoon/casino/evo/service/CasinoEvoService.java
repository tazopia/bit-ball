package spoon.casino.evo.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evo.domain.CasinoEvoCmd;
import spoon.casino.evo.domain.CasinoEvoConfig;
import spoon.casino.evo.entity.CasinoEvoExchange;
import spoon.casino.evo.entity.CasinoEvoUser;
import spoon.casino.evo.entity.QCasinoEvoExchange;
import spoon.casino.evo.repository.CasinoEvoExchangeRepository;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.entity.JsonConfig;
import spoon.config.service.ConfigService;
import spoon.gameZone.ZoneConfig;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Service
public class CasinoEvoService {

    private final ConfigService jsonConfigService;

    private final CasinoEvoApiService casinoEvoApiService;

    private final CasinoEvoExchangeRepository casinoEvoExchangeRepository;

    private static final QCasinoEvoExchange q = QCasinoEvoExchange.casinoEvoExchange;

    @PostConstruct
    public void init() {
        JsonConfig casinoConfig = jsonConfigService.getOne("casino_evo");
        if (casinoConfig == null) {
            ZoneConfig.setCasinoEvo(new CasinoEvoConfig());
        } else {
            ZoneConfig.setCasinoEvo(JsonUtils.toModel(casinoConfig.getJson(), CasinoEvoConfig.class));
        }
    }

    @Transactional
    public boolean updateConfig(CasinoEvoConfig config) {
        CasinoEvoConfig org = ZoneConfig.getCasinoEvo();
        org.setApiId(config.getApiId());
        org.setApiKey(config.getApiKey());

        try {
            jsonConfigService.updateZoneConfig("casino_evo", JsonUtils.toString(org));
            ZoneConfig.setCasinoEvo(org);
        } catch (RuntimeException e) {
            log.error("카지노 환경설정 업데이트에 실패하였습니다.", e);
            return false;
        }
        return true;
    }

    public Page<CasinoEvoExchange> page(CasinoEvoCmd.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.contains(command.getUserid()));
        }

        if (StringUtils.notEmpty(command.getCasinoId())) {
            builder.and(q.casinoId.contains(command.getCasinoId()));
        }

        if (StringUtils.notEmpty(command.getTransaction())) {
            builder.and(q.trans.eq(command.getTransaction()));
        }

        if (StringUtils.notEmpty(command.getGameType())) {
            builder.and(q.gameType.eq(command.getGameType()));
        }

        if (StringUtils.notEmpty(command.getRound())) {
            builder.and(q.round.eq(command.getRound()));
        }

        return casinoEvoExchangeRepository.findAll(builder, pageable);
    }

    public String getGameUrl() {
        CasinoEvoUser evoUser = casinoEvoApiService.getCasinoUser(WebUtils.userid());
        if (evoUser == null) return "";

        return casinoEvoApiService.getGameUrl(evoUser.getCasinoId());
    }
}
