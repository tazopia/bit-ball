package spoon.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import spoon.bot.sports.best.domain.BotBest;
import spoon.bot.sports.bet365.domain.BotBet365;
import spoon.bot.sports.ferrari.domain.BotFerrari;
import spoon.bot.sports.sports.domain.BotSports;
import spoon.gameZone.dog.Dog;
import spoon.gameZone.soccer.Soccer;
import spoon.inPlay.odds.entity.InPlayGame;
import spoon.sun.domain.SunResponseDto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 객체를 JSON String으로 변환한다.
     *
     * @param clazz Class 객체
     * @return 변환에 성공하면 JSON 스트링을 실패할 경우 배열형태면 빈배열 JSON 을, 클래스 형태의 경우 빈 JSON 을 반환한다.
     */
    public static String toString(Object clazz) {
        try {
            return mapper.writeValueAsString(clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 객체 변환 실패 - {}", e.getMessage());
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }

        return null;
    }

    /**
     * JSON 객체를 모델로 변환한다.
     *
     * @param json      JSON 형태 문자열
     * @param valueType 변환할 Class 형식
     * @param <T>       변환할 클래스
     * @return 변환에 성공하면 class 객체를 실패하면 null 을 반환한다.
     */
    public static <T> T toModel(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            log.error("객체 변환 실패 : {}, {}", e.getMessage(), json);
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }
        return null;
    }

    /**
     * 게임봇 List<BotBet365> 반환
     *
     * @param json
     * @return
     */
    public static List<BotBet365> toBet365List(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<BotBet365>>() {
            });
        } catch (IOException e) {
            log.warn("List<BotBet365> 객체 변환 실패 : {}", e.getMessage());
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }
        return null;
    }

    /**
     * 게임봇 List<BotBest> 반환
     *
     * @param json
     * @return
     */
    public static List<BotBest> toBestList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<BotBest>>() {
            });
        } catch (IOException e) {
            log.warn("List<BotBest> 객체 변환 실패 : {}", e.getMessage());
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }
        return null;
    }

    /**
     * 가상축구 List<Soccer> 반환
     *
     * @param json
     * @return
     */
    public static List<Soccer> toSoccerList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<Soccer>>() {
            });
        } catch (IOException e) {
            log.warn("List<Soccer> 객체 변환 실패 : {}", e.getMessage());
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }
        return null;
    }

    /**
     * 개경주 List<Dog> 반환
     *
     * @param json
     * @return
     */
    public static List<Dog> toDogList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<Dog>>() {
            });
        } catch (IOException e) {
            log.warn("List<Dog> 객체 변환 실패 : {}", e.getMessage());
            log.warn("{}", ErrorUtils.traceAll(e.getStackTrace()));
        }
        return null;
    }

    public static List<BotSports> toSportsBotList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<BotSports>>() {
            });
        } catch (IOException e) {
            log.warn("List<BotSports> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<BotFerrari> toFerrariList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<BotFerrari>>() {
            });
        } catch (IOException e) {
            log.warn("List<BotSports> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<SunResponseDto.Balance> toSunBalanceList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<SunResponseDto.Balance>>() {
            });
        } catch (IOException e) {
            log.warn("List<SunResponseDto.Balance> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<SunResponseDto.Credit> toSunCreditList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<SunResponseDto.Credit>>() {
            });
        } catch (IOException e) {
            log.warn("List<SunResponseDto.Credit> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<SunResponseDto.Debit> toSunDebit(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<SunResponseDto.Debit>>() {
            });
        } catch (IOException e) {
            log.warn("List<SunResponseDto.Debit> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<SunResponseDto.CreateUser> toSunUserList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<SunResponseDto.CreateUser>>() {
            });
        } catch (IOException e) {
            log.warn("List<SunResponseDto.CreateUser> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }

    public static List<InPlayGame> toInPlayList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<InPlayGame>>() {
            });
        } catch (IOException e) {
            log.warn("List<InPlayGame> 객체 변환에 실패하였습니다.", e);
        }
        return Collections.emptyList();
    }
}
