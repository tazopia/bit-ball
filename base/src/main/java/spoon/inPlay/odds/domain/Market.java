package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Market {

    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Providers")
    private List<Provider> providers;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Provider {

        @JsonProperty("Id")
        private Long id;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Bets")
        private List<Bet> bets;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Bet {

        @JsonProperty("Id")
        private Long id;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Line")
        private String line = "";

        @JsonProperty("BaseLine")
        private String baseLine = "";

        @JsonProperty("Status")
        private int status;

        @JsonProperty("StartPrice")
        private double startPrice;

        @JsonProperty("Price")
        private double price;

        /**
         * 0 : 진행중(생성시 기본 DB 값)
         * -1 : 취소
         * 1 : 미당첨
         * 2 : 당첨
         * 3 : 적특
         * 4 : 50% 미당첨
         * 5 : 50% 당첨
         * 4,5 의 경우는 주로 0.25 핸디캡 등에 사용 됩니다
         */
        @JsonProperty("Settlement")
        private int settlement;
    }
}
