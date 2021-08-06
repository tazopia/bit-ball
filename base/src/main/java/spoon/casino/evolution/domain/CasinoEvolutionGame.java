package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvolutionGame {

    private String title;
    private String type;
    private String id;
    //private String vendor;
    @JsonProperty("thumbnail")
    private String img;

    public String getVendor() {
        return this.id.split("_")[0];
    }
}
