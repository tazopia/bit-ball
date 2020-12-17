package spoon.support.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AjaxResult {

    private boolean success;
    // close: 창닫기, reload: 리로딩 하기, not empty: 해당 url 로 이동
    private String url = "";
    private String message = "";
    // 특별히 가져가야 할 값들이 있을 경우 사용
    private String value = "";

    public AjaxResult(boolean success) {
        this.success = success;
    }

    public AjaxResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
