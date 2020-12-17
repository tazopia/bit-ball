package spoon.bot.sports.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TargetCode {

    SPORTS("images/sports/"),
    LEAGUE("images/league/"),
    NOTICE("images/notice/");

    private String path;
}
