package spoon.gameZone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import spoon.bot.zone.service.GameBotParsing;
import spoon.casino.evo.domain.CasinoEvoConfig;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.config.entity.JsonConfig;
import spoon.config.repository.JsonRepository;
import spoon.gameZone.KenoLadder.KenoLadderConfig;
import spoon.gameZone.aladdin.AladdinConfig;
import spoon.gameZone.baccarat.BaccaratConfig;
import spoon.gameZone.bitcoin1.bot.Bitcoin1Parsing;
import spoon.gameZone.bitcoin1.domain.Bitcoin1Config;
import spoon.gameZone.bitcoin3.bot.Bitcoin3Parsing;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Config;
import spoon.gameZone.bitcoin5.bot.Bitcoin5Parsing;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Config;
import spoon.gameZone.crownBaccarat.CrownBaccaratConfig;
import spoon.gameZone.crownOddeven.CrownOddevenConfig;
import spoon.gameZone.crownSutda.CrownSutdaConfig;
import spoon.gameZone.dari.DariConfig;
import spoon.gameZone.dog.DogConfig;
import spoon.gameZone.keno.bot.KenoParsing;
import spoon.gameZone.keno.domain.KenoConfig;
import spoon.gameZone.ladder.LadderConfig;
import spoon.gameZone.lowhi.LowhiConfig;
import spoon.gameZone.luck.LuckConfig;
import spoon.gameZone.newSnail.NewSnailConfig;
import spoon.gameZone.oddeven.OddevenConfig;
import spoon.gameZone.power.PowerConfig;
import spoon.gameZone.powerLadder.PowerLadderConfig;
import spoon.gameZone.snail.SnailConfig;
import spoon.gameZone.soccer.SoccerConfig;
import spoon.gate.domain.GateConfig;
import spoon.sun.domain.SunConfig;

@AllArgsConstructor
@Component
public class ZoneConfig {

    private JsonRepository jsonRepository;

    private GameBotParsing ladderParsing;

    private GameBotParsing snailParsing;

    private GameBotParsing newSnailParsing;

    private GameBotParsing dariParsing;

    private GameBotParsing powerParsing;

    private GameBotParsing powerLadderParsing;

    private GameBotParsing kenoLadderParsing;

    private GameBotParsing aladdinParsing;

    private GameBotParsing lowhiParsing;

    private GameBotParsing oddevenParsing;

    private GameBotParsing baccaratParsing;

    private GameBotParsing soccerParsing;

    private GameBotParsing dogParsing;

    private GameBotParsing luckParsing;

    private GameBotParsing crownOddevenParsing;

    private GameBotParsing crownBaccaratParsing;

    private GameBotParsing crownSutdaParsing;

    private KenoParsing kenoParsing;

    private Bitcoin1Parsing bitcoin1Parsing;

    private Bitcoin3Parsing bitcoin3Parsing;

    private Bitcoin5Parsing bitcoin5Parsing;

    public void loadZoneConfig() {
        initLadderConfig();
        initSnailConfig();
        initNewSnailConfig();
        initDariConfig();
        initPowerConfig();
        initPowerLadderConfig();
        initKenoLadderConfig();
        initKenoConfig();
        initAladdinConfig();
        initLowhiConfig();
        initOddevenConfig();
        initBaccaratConfig();
        initSoccerConfig();
        initDogConfig();
        initLuckConfig();

        // 크라운 게임
        initCrownOddevenConfig();
        initCrownBaccaratConfig();
        initCrownSutdaConfig();

        // 비트코인
        initBitcoin1Config();
        initBitcoin3Config();
        initBitcoin5Config();
    }

    private void initCrownSutdaConfig() {
        // 섰다
        JsonConfig crownSutdaConfig = jsonRepository.findOne("cw_sutda");
        if (crownSutdaConfig == null) {
            ZoneConfig.setCrownSutda(new CrownSutdaConfig());
        } else {
            ZoneConfig.setCrownSutda(JsonUtils.toModel(crownSutdaConfig.getJson(), CrownSutdaConfig.class));
        }

        if (ZoneConfig.getCrownSutda().isEnabled() && Config.getSysConfig().getZone().isCrownSutda()) {
            crownSutdaParsing.parsingGame();
            crownSutdaParsing.checkResult();
        }
    }

    private void initCrownBaccaratConfig() {
        // 바카라
        JsonConfig crownBaccaratConfig = jsonRepository.findOne("cw_baccarat");
        if (crownBaccaratConfig == null) {
            ZoneConfig.setCrownBaccarat(new CrownBaccaratConfig());
        } else {
            ZoneConfig.setCrownBaccarat(JsonUtils.toModel(crownBaccaratConfig.getJson(), CrownBaccaratConfig.class));
        }

        if (ZoneConfig.getCrownBaccarat().isEnabled() && Config.getSysConfig().getZone().isCrownBaccarat()) {
            crownBaccaratParsing.parsingGame();
            crownBaccaratParsing.checkResult();
        }
    }

    private void initCrownOddevenConfig() {
        // 홀짝
        JsonConfig crownOddevenConfig = jsonRepository.findOne("cw_oddeven");
        if (crownOddevenConfig == null) {
            ZoneConfig.setCrownOddeven(new CrownOddevenConfig());
        } else {
            ZoneConfig.setCrownOddeven(JsonUtils.toModel(crownOddevenConfig.getJson(), CrownOddevenConfig.class));
        }

        if (ZoneConfig.getCrownOddeven().isEnabled() && Config.getSysConfig().getZone().isCrownOddeven()) {
            crownOddevenParsing.parsingGame();
            crownOddevenParsing.checkResult();
        }
    }

    private void initLuckConfig() {
        // 세븐럭
        JsonConfig luckConfig = jsonRepository.findOne("luck");
        if (luckConfig == null) {
            ZoneConfig.setLuck(new LuckConfig());
        } else {
            ZoneConfig.setLuck(JsonUtils.toModel(luckConfig.getJson(), LuckConfig.class));
        }

        if (ZoneConfig.getLuck().isEnabled() && Config.getSysConfig().getZone().isLuck()) {
            luckParsing.parsingGame();
            luckParsing.checkResult();
        }
    }

    private void initDogConfig() {
        // 개경주
        JsonConfig dogConfig = jsonRepository.findOne("dog");
        if (dogConfig == null) {
            ZoneConfig.setDog(new DogConfig());
        } else {
            ZoneConfig.setDog(JsonUtils.toModel(dogConfig.getJson(), DogConfig.class));
        }

        if (ZoneConfig.getDog().isEnabled() && Config.getSysConfig().getZone().isDog()) {
            dogParsing.parsingGame();
            dogParsing.checkResult();
        }
    }

    private void initSoccerConfig() {
        // 가상축구
        JsonConfig soccerConfig = jsonRepository.findOne("soccer");
        if (soccerConfig == null) {
            ZoneConfig.setSoccer(new SoccerConfig());
        } else {
            ZoneConfig.setSoccer(JsonUtils.toModel(soccerConfig.getJson(), SoccerConfig.class));
        }

        if (ZoneConfig.getSoccer().isEnabled() && Config.getSysConfig().getZone().isSoccer()) {
            soccerParsing.parsingGame();
            soccerParsing.checkResult();
        }
    }

    private void initBaccaratConfig() {
        // 바카라
        JsonConfig baccaratConfig = jsonRepository.findOne("baccarat");
        if (baccaratConfig == null) {
            ZoneConfig.setBaccarat(new BaccaratConfig());
        } else {
            ZoneConfig.setBaccarat(JsonUtils.toModel(baccaratConfig.getJson(), BaccaratConfig.class));
        }

        if (ZoneConfig.getBaccarat().isEnabled() && Config.getSysConfig().getZone().isBaccarat()) {
            baccaratParsing.parsingGame();
            baccaratParsing.checkResult();
        }
    }

    private void initOddevenConfig() {
        // 홀짝
        JsonConfig oddevenConfig = jsonRepository.findOne("oddeven");
        if (oddevenConfig == null) {
            ZoneConfig.setOddeven(new OddevenConfig());
        } else {
            ZoneConfig.setOddeven(JsonUtils.toModel(oddevenConfig.getJson(), OddevenConfig.class));
        }

        if (ZoneConfig.getOddeven().isEnabled() && Config.getSysConfig().getZone().isOddeven()) {
            oddevenParsing.parsingGame();
            oddevenParsing.checkResult();
        }
    }

    private void initLowhiConfig() {
        // 로하이
        JsonConfig lowhiConfig = jsonRepository.findOne("lowhi");
        if (lowhiConfig == null) {
            ZoneConfig.setLowhi(new LowhiConfig());
        } else {
            ZoneConfig.setLowhi(JsonUtils.toModel(lowhiConfig.getJson(), LowhiConfig.class));
        }

        if (ZoneConfig.getLowhi().isEnabled() && Config.getSysConfig().getZone().isLowhi()) {
            lowhiParsing.parsingGame();
            lowhiParsing.checkResult();
        }
    }

    private void initAladdinConfig() {
        // 알라딘
        JsonConfig aladdinConfig = jsonRepository.findOne("aladdin");
        if (aladdinConfig == null) {
            ZoneConfig.setAladdin(new AladdinConfig());
        } else {
            ZoneConfig.setAladdin(JsonUtils.toModel(aladdinConfig.getJson(), AladdinConfig.class));
        }

        if (ZoneConfig.getAladdin().isEnabled() && Config.getSysConfig().getZone().isAladdin()) {
            aladdinParsing.parsingGame();
            aladdinParsing.checkResult();
        }
    }

    private void initKenoConfig() {
        // 스피드 키노
        JsonConfig kenoConfig = jsonRepository.findOne("keno");
        if (kenoConfig == null) {
            ZoneConfig.setKeno(new KenoConfig());
        } else {
            ZoneConfig.setKeno(JsonUtils.toModel(kenoConfig.getJson(), KenoConfig.class));
        }

        if (ZoneConfig.getKeno().isEnabled() && Config.getSysConfig().getZone().isKeno()) {
            kenoParsing.parsingGame();
            kenoParsing.checkResult();
        }
    }

    private void initKenoLadderConfig() {
        // 키노사다리
        JsonConfig kenoLadderConfig = jsonRepository.findOne("keno_ladder");
        if (kenoLadderConfig == null) {
            ZoneConfig.setKenoLadder(new KenoLadderConfig());
        } else {
            ZoneConfig.setKenoLadder(JsonUtils.toModel(kenoLadderConfig.getJson(), KenoLadderConfig.class));
        }

        if (ZoneConfig.getKenoLadder().isEnabled() && Config.getSysConfig().getZone().isKenoLadder()) {
            kenoLadderParsing.parsingGame();
            kenoLadderParsing.checkResult();
        }
    }

    private void initPowerLadderConfig() {
        // 파워볼
        JsonConfig powerLadderConfig = jsonRepository.findOne("power_ladder");
        if (powerLadderConfig == null) {
            ZoneConfig.setPowerLadder(new PowerLadderConfig());
        } else {
            ZoneConfig.setPowerLadder(JsonUtils.toModel(powerLadderConfig.getJson(), PowerLadderConfig.class));
        }

        if (ZoneConfig.getPowerLadder().isEnabled() && Config.getSysConfig().getZone().isPowerLadder()) {
            powerLadderParsing.parsingGame();
            powerLadderParsing.checkResult();
        }
    }

    private void initPowerConfig() {
        // 파워볼
        JsonConfig powerConfig = jsonRepository.findOne("power");
        if (powerConfig == null) {
            ZoneConfig.setPower(new PowerConfig());
        } else {
            ZoneConfig.setPower(JsonUtils.toModel(powerConfig.getJson(), PowerConfig.class));
        }

        if (ZoneConfig.getPower().isEnabled() && Config.getSysConfig().getZone().isPower()) {
            powerParsing.parsingGame();
            powerParsing.checkResult();
        }
    }

    private void initDariConfig() {
        // 다리다리
        JsonConfig dariConfig = jsonRepository.findOne("dari");
        if (dariConfig == null) {
            ZoneConfig.setDari(new DariConfig());
        } else {
            ZoneConfig.setDari(JsonUtils.toModel(dariConfig.getJson(), DariConfig.class));
        }

        if (ZoneConfig.getDari().isEnabled() && Config.getSysConfig().getZone().isDari()) {
            dariParsing.parsingGame();
            dariParsing.checkResult();
        }
    }

    private void initSnailConfig() {
        // 달팽이
        JsonConfig snailConfig = jsonRepository.findOne("snail");
        if (snailConfig == null) {
            ZoneConfig.setSnail(new SnailConfig());
        } else {
            ZoneConfig.setSnail(JsonUtils.toModel(snailConfig.getJson(), SnailConfig.class));
        }

        if (ZoneConfig.getSnail().isEnabled() && Config.getSysConfig().getZone().isSnail()) {
            snailParsing.parsingGame();
            snailParsing.checkResult();
        }
    }

    private void initNewSnailConfig() {
        // 달팽이
        JsonConfig newSnailConfig = jsonRepository.findOne("new_snail");
        if (newSnailConfig == null) {
            ZoneConfig.setNewSnail(new NewSnailConfig());
        } else {
            ZoneConfig.setNewSnail(JsonUtils.toModel(newSnailConfig.getJson(), NewSnailConfig.class));
        }

        if (ZoneConfig.getNewSnail().isEnabled() && Config.getSysConfig().getZone().isNewSnail()) {
            newSnailParsing.parsingGame();
            newSnailParsing.checkResult();
        }
    }

    private void initLadderConfig() {
        // 사다리
        JsonConfig ladderConfig = jsonRepository.findOne("ladder");
        if (ladderConfig == null) {
            ZoneConfig.setLadder(new LadderConfig());
        } else {
            ZoneConfig.setLadder(JsonUtils.toModel(ladderConfig.getJson(), LadderConfig.class));
        }

        if (ZoneConfig.getLadder().isEnabled() && Config.getSysConfig().getZone().isLadder()) {
            ladderParsing.parsingGame();
            ladderParsing.checkResult();
        }
    }

    private void initBitcoin1Config() {
        // 비트코인 1분
        JsonConfig config = jsonRepository.findOne("bitcoin1");
        if (config == null) {
            ZoneConfig.setBitcoin1(new Bitcoin1Config());
        } else {
            ZoneConfig.setBitcoin1(JsonUtils.toModel(config.getJson(), Bitcoin1Config.class));
        }

        if (ZoneConfig.getBitcoin1().isEnabled() && Config.getSysConfig().getZone().isBitcoin1()) {
            bitcoin1Parsing.addGame();
            bitcoin1Parsing.checkResult();
        }
    }

    private void initBitcoin3Config() {
        // 비트코인 3분
        JsonConfig config = jsonRepository.findOne("bitcoin3");
        if (config == null) {
            ZoneConfig.setBitcoin3(new Bitcoin3Config());
        } else {
            ZoneConfig.setBitcoin3(JsonUtils.toModel(config.getJson(), Bitcoin3Config.class));
        }

        if (ZoneConfig.getBitcoin3().isEnabled() && Config.getSysConfig().getZone().isBitcoin3()) {
            bitcoin3Parsing.addGame();
            bitcoin3Parsing.checkResult();
        }
    }

    private void initBitcoin5Config() {
        // 비트코인 5분
        JsonConfig config = jsonRepository.findOne("bitcoin5");
        if (config == null) {
            ZoneConfig.setBitcoin5(new Bitcoin5Config());
        } else {
            ZoneConfig.setBitcoin5(JsonUtils.toModel(config.getJson(), Bitcoin5Config.class));
        }

        if (ZoneConfig.getBitcoin5().isEnabled() && Config.getSysConfig().getZone().isBitcoin5()) {
            bitcoin5Parsing.addGame();
            bitcoin5Parsing.checkResult();
        }
    }

    @Getter
    @Setter
    private static LadderConfig ladder;

    @Getter
    @Setter
    private static SnailConfig snail;

    @Getter
    @Setter
    private static NewSnailConfig newSnail;

    @Getter
    @Setter
    private static DariConfig dari;

    @Getter
    @Setter
    private static PowerConfig power;

    @Getter
    @Setter
    private static PowerLadderConfig powerLadder;

    @Getter
    @Setter
    private static KenoLadderConfig kenoLadder;

    @Getter
    @Setter
    private static KenoConfig keno;

    @Getter
    @Setter
    private static AladdinConfig aladdin;

    @Getter
    @Setter
    private static LowhiConfig lowhi;

    @Getter
    @Setter
    private static OddevenConfig oddeven;

    @Getter
    @Setter
    private static BaccaratConfig baccarat;

    @Getter
    @Setter
    private static SoccerConfig soccer;

    @Getter
    @Setter
    private static DogConfig dog;

    @Getter
    @Setter
    private static LuckConfig luck;

    @Getter
    @Setter
    private static GateConfig gate;

    @Getter
    @Setter
    private static SunConfig sun;

    @Getter
    @Setter
    private static CrownOddevenConfig crownOddeven;

    @Getter
    @Setter
    private static CrownBaccaratConfig crownBaccarat;

    @Getter
    @Setter
    private static CrownSutdaConfig crownSutda;

    @Getter
    @Setter
    private static Bitcoin1Config bitcoin1;

    @Getter
    @Setter
    private static Bitcoin3Config bitcoin3;

    @Getter
    @Setter
    private static Bitcoin5Config bitcoin5;

    @Getter
    @Setter
    private static CasinoEvoConfig casinoEvo;
}
