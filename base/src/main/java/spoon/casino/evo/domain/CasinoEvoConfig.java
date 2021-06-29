package spoon.casino.evo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvoConfig {

    private String apiUrl = "http://ok-7878.com";

    private String apiId = "";

    private String apiKey = "";

    private String prefix = "bit";

    // 유저 생성하기
    private String createUser = "%s/api/createAccount";

    // 게임키 얻기
    private String generateSession = "%s/api/generateSession";

    // 게임 호출 주소얻기
    private String getGameUrl = "%s/api/getGameUrl";

    // 게임 상세 내역 얻기
    private String getGameDetail = "%s/api/getGameDetail";
}
