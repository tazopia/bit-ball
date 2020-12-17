package spoon.sun.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SunConfig {

    private String masterId = "";

    private String masterPassword = "";

    private String prefix = "kib";

    // -------------------------------------------------------------------------------

    private String sunSlotUrl = "http://gameplay.api-gamingsun.com/apiService/Success_Slot.html?G_NUM=%s";

    private String sunCasinoUrl = "http://gameplay.api-gamingsun.com/apiService/Success_Live.html?G_NUM=%s";

    private String createUser = "http://createuser.api-gamingsun.com/CreateID_Get.html?MASTER_ID=%s&MASTER_PASSWD=%s&USER_ID=%s";

    private String balanceUser = "http://chip.api-gamingsun.com/Balance_Chip.html?MASTER_ID=%s&MASTER_PASSWD=%s&USER_ID=%s&GAMENO=%d";

    private String balanceAll = "http://chip.api-gamingsun.com/Balance_Chip.html?MASTER_ID=%s&MASTER_PASSWD=%s&USER_ID=%s";

    private String sendMoneyUrl = "http://chip.api-gamingsun.com/Create_Chip.html?MASTER_ID=%s&MASTER_PASSWD=%s&USER_ID=%s&GAMENO=%d&Member_Amount=%d";

    private String receiveMoneyUrl = "http://chip.api-gamingsun.com/Debit_Chip.html?MASTER_ID=%s&MASTER_PASSWD=%s&USER_ID=%s&GAMENO=%d&Member_R_Amount=%d";

}
