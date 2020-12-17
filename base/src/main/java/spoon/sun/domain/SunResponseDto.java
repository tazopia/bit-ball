package spoon.sun.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class SunResponseDto {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateUser {
        @JsonProperty("RespCode")
        private String respCode;

        @JsonProperty("G_ID")
        private String gId; // 생성된ID

        @JsonProperty("G_NUM")
        private String gNum; // 게임접속 SN넘버
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Balance {
        @JsonProperty("RespCode")
        private String respCode;

        @JsonProperty("Msg")
        private String msg; // 생성된 ID

        @JsonProperty("ErrorMsg")
        private String errorMsg; // 에러 메시지지

        @JsonProperty("MONEY")
        private long money; // 잔여금액

        @JsonProperty("Name")
        private String name; // 게임명
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Credit {
        @JsonProperty("RespCode")
        private String respCode;

        @JsonProperty("Msg")
        private String Msg; //생성된 ID

        @JsonProperty("AMOUNT")
        private long amount; //이동금액
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Debit {
        @JsonProperty("RespCode")
        private String respCode;

        @JsonProperty("Msg")
        private String Msg; //생성된 ID

        @JsonProperty("R_AMOUNT")
        private long amount; //전환금액
    }

}
