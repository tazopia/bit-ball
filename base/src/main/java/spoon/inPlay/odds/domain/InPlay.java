package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InPlay {

    @JsonProperty("Header")
    private Header header;

    @JsonProperty("Body")
    private Body body;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Header {

        @JsonProperty("Type")
        private int type;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Body {

        @JsonProperty("Events")
        private List<Event> events;
    }
}
