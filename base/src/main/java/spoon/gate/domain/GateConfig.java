package spoon.gate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GateConfig {

    private String gateUrl = "http://gold.gateonbet.com:17030";

    private String gateUserid = "satoo";

    private String gateAgency = "satoo1";

    private String gateKey = "";

}
