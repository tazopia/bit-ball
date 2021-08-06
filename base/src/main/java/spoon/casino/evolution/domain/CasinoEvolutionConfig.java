package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvolutionConfig {

    private String apiUrl = "https://evolutionapi.com/api";

    private String apiKey = "VQJUhOKAdUENkC4nnDKFTsGIOKMRmTpPaX10bgPU";

    private String prefix = "bit";

    // -------------------------------------------------------------------------------

    private String createUser = this.apiUrl + "/user/create?username=%s&nickname=%s";

    private String user = this.apiUrl + "/user?username=%s";

    private String token = this.apiUrl + "/user/refresh-token?username=%s";

    private String gameList = this.apiUrl + "/game-list";

    private String lobbyList = this.apiUrl + "/lobby-list";

    private String gameUrl = this.apiUrl + "/open";

    private String addBalance = this.apiUrl + "/user/add-balance?username=%s&amount=%d";

    private String subBalance = this.apiUrl + "/user/sub-balance-all?username=%s";

    private String exchange = this.apiUrl + "/user/exchange-point-all?username=%s";

}
