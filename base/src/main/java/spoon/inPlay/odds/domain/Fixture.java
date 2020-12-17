package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Fixture {


    @JsonProperty("Sport")
    private FixtureBody sports;

    @JsonProperty("Location")
    private FixtureBody location;

    @JsonProperty("League")
    private FixtureBody league;

    @JsonProperty("StartDate")
    private Date startDate;

    @JsonProperty("LastUpdate")
    private Date lastUpdate;

    /**
     * 1 : 경기 전
     * 2 : 경기 진행 중
     * 3 : 경기 종료
     * 4 : 경기 취소
     * 5 : 경기 지연
     * 6 : 경기 중단
     * 7 : 버려진 경기
     * 8 : 신호를 잃은 경기
     * 9 : 경기 시작 30분전 INPLAY 경기 진행 알림
     */
    @JsonProperty("Status")
    private int status;

    @JsonProperty("Participants")
    private List<FixtureBody> participants;

    // 팀명을 가져온다.
    public String hname() {
        return participants.stream().filter(p -> "1".equals(p.position))
                .map(FixtureBody::getName).collect(Collectors.joining("/"));
    }

    public String aname() {
        return participants.stream().filter(p -> "2".equals(p.position))
                .map(FixtureBody::getName).collect(Collectors.joining("/"));
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class FixtureBody {
        @JsonProperty("Id")
        private long id;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Position")
        private String position;
    }
}
