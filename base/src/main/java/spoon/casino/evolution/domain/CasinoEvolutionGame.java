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
    private String provider;
    private String vendor;

    @JsonProperty("thumbnail")
    private String img;
}
