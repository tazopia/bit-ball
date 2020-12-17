package spoon.bot.balance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GateResult {

    @JsonProperty("result")
    private int flag;

    @JsonProperty("tx_code")
    private int code;

    @JsonIgnore
    public String getMessage() {
        switch (flag) {
            case 1:
                return "베팅등록 성공";
            case 100:
                return "서비스 점검중";
            case 101:
                return "보유캐시 부족";
            case 201:
                return "베팅시간 초과";
            case 901:
                return "API 아이디 또는 API Key 인증오류";
            case 902:
                return "파라미터 오류";
            case 903:
                return "최소 베팅금액 미만으로 요청";
            case 999:
                return "기타오류";
            default:
                return "알수없는 오류. 관리자에게 문의 하세요";
        }
    }
}
