package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvolutionConfig {

    private String apiUrl = "https://honorlink.org/api";

    private String apiKey = "VQJUhOKAdUENkC4nnDKFTsGIOKMRmTpPaX10bgPU";

    private String prefix = "bit";

    // -------------------------------------------------------------------------------

    private String createUser = this.getApiUrl() + "/user/create?username=%s&nickname=%s";

    private String user = this.getApiUrl() + "/user?username=%s";

    private String token = this.getApiUrl() + "/user/refresh-token?username=%s";

    private String gameList = this.getApiUrl() + "/game-list";

    private String lobbyList = this.getApiUrl() + "/lobby-list";

    private String gameUrl = this.getApiUrl() + "/open";

    private String addBalance = this.getApiUrl() + "/user/add-balance?username=%s&amount=%d";

    private String subBalance = this.getApiUrl() + "/user/sub-balance-all?username=%s";

    private String exchange = this.getApiUrl() + "/user/exchange-point-all?username=%s";

    private String transaction = this.getApiUrl() + "/transaction-list?start=%s&start_transaction_id=%d&perPage=1000";

}
