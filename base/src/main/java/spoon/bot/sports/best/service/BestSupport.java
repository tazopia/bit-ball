package spoon.bot.sports.best.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BestSupport {

    public static String getSpecial(String n) {
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(n);
        if (m.find()) {
            String s = m.group(1);
            if (s.contains("홀")) {
                return s + "/짝";
            } else if (s.contains("득점")) {
                return s + "/무득점";
            } else if (s.contains("오버")) {
                return s + "/언더";
            } else {
                return s;
            }
        }
        return "";
    }
}
