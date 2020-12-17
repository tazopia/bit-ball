package spoon.config.service;

import spoon.config.domain.GameConfig;
import spoon.config.domain.SiteConfig;
import spoon.config.domain.SysConfig;
import spoon.config.entity.JsonConfig;
import spoon.support.web.AjaxResult;

public interface ConfigService {

    void updateSysConfig(SysConfig sysConfig);

    void updateGameConfig(GameConfig gameConfig);

    void updateSiteConfig(SiteConfig siteConfig);

    void updateZoneConfig(String zone, String json);

    AjaxResult updateRolling(JsonConfig jsonConfig);

    JsonConfig getOne(String code);

}
