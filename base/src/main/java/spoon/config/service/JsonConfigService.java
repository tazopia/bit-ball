package spoon.config.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.config.domain.GameConfig;
import spoon.config.domain.SiteConfig;
import spoon.config.domain.SysConfig;
import spoon.config.entity.JsonConfig;
import spoon.config.repository.JsonRepository;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Service
public class JsonConfigService implements ConfigService {

    private JsonRepository jsonRepository;

    @Transactional
    @Override
    public void updateSysConfig(SysConfig sysConfig) {
        Config.setSysConfig(sysConfig);

        JsonConfig jsonConfig = jsonRepository.findOne("sys");
        if (jsonConfig == null) jsonConfig = new JsonConfig("sys");
        jsonConfig.setJson(JsonUtils.toString(sysConfig));
        jsonRepository.saveAndFlush(jsonConfig);
    }

    @Transactional
    @Override
    public void updateGameConfig(GameConfig gameConfig) {
        Config.setGameConfig(gameConfig);

        JsonConfig jsonConfig = jsonRepository.findOne("game");
        if (jsonConfig == null) jsonConfig = new JsonConfig("game");
        jsonConfig.setJson(JsonUtils.toString(gameConfig));
        jsonRepository.saveAndFlush(jsonConfig);
    }

    @Transactional
    @Override
    public void updateSiteConfig(SiteConfig siteConfig) {
        Config.setSiteConfig(siteConfig);

        JsonConfig jsonConfig = jsonRepository.findOne("site");
        if (jsonConfig == null) jsonConfig = new JsonConfig("site");
        jsonConfig.setJson(JsonUtils.toString(siteConfig));
        jsonRepository.saveAndFlush(jsonConfig);
    }

    @Transactional
    @Override
    public void updateZoneConfig(String zone, String json) {
        JsonConfig jsonConfig = jsonRepository.findOne(zone);
        if (jsonConfig == null) jsonConfig = new JsonConfig(zone);
        jsonConfig.setJson(json);
        jsonRepository.saveAndFlush(jsonConfig);
    }

    @Transactional
    @Override
    public AjaxResult updateRolling(JsonConfig jsonConfig) {
        jsonConfig.setCode("rolling");
        Config.setRolling(jsonConfig);
        jsonRepository.saveAndFlush(jsonConfig);
        return new AjaxResult(true, "상단 한줄 공지를 업데이트 했습니다.");
    }

    @Override
    public JsonConfig getOne(String code) {
        return jsonRepository.findOne(code);
    }

}
