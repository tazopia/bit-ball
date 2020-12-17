package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.event.domain.DailyCalendar;

import java.util.List;

public interface DailyEventMapper {

    Long todayCash(@Param("userid") String userid, @Param("start") String start, @Param("end") String end);

    Long hasCheckDaily(@Param("userid") String userid, @Param("sdate") String sdate);

    void addDaily(@Param("userid") String userid, @Param("nickname") String nickname, @Param("sdate") String sdate, @Param("daily") int daily);

    Integer getYesterday(@Param("userid") String userid, @Param("sdate") String yesterday);

    List<DailyCalendar> calendar(@Param("userid") String userid, @Param("month") String month);
}
