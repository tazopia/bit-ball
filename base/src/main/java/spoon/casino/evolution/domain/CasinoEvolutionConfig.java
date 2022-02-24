package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvolutionConfig {

    private String apiUrl = "https://api.honorlink.org/api";

    private String apiKey = "dJ5jeptySxyxhW4IOnWzehzt1o4aBbvvmyhdPPkO";

    private String prefix = "bit";

    // -------------------------------------------------------------------------------

    public String getCreateUser() {
        return this.getApiUrl() + "/user/create?username=%s&nickname=%s";
    }

    public String getUser() {
        return this.getApiUrl() + "/user?username=%s";
    }

    public String getToken() {
        return this.getApiUrl() + "/user/refresh-token?username=%s";
    }

    public String getGameList() {
        return this.getApiUrl() + "/game-list";
    }

    public String getLobbyList() {
        return this.getApiUrl() + "/lobby-list";
    }

    public String getGameUrl() {
        return this.getApiUrl() + "/open";
    }

    public String getAddBalance() {
        return this.getApiUrl() + "/user/add-balance?username=%s&amount=%d";
    }

    public String getSubBalance() {
        return this.getApiUrl() + "/user/sub-balance-all?username=%s";
    }

    public String getExchange() {
        return this.getApiUrl() + "/user/exchange-point-all?username=%s";
    }

    public String getTransaction() {
        return this.getApiUrl() + "/transaction-list-simple?start=%s&start_transaction_id=%d&perPage=1000";
    }

    public String getMyInfo() {
        return this.getApiUrl() + "/my-info";
    }

}
