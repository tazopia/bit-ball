package spoon.common.utils;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.support.security.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

@Slf4j
public class WebUtils {

    public static LoginUser loginUser() {
        try {
            return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static CurrentUser user() {
        try {
            return ((LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        } catch (NullPointerException e) {
            log.info("현재 접속 유저의 정보를 가져올 수 없습니다. : {}", e.getMessage());
            return null;
        }
    }

    public static User user(CurrentUser currentUser) {
        User user = new User();
        if (currentUser == null) return user;

        user.setUserid(currentUser.getUserid());
        user.setNickname(currentUser.getNickname());
        user.setAgency1(currentUser.getAgency1());
        user.setAgency2(currentUser.getAgency2());
        user.setAgency3(currentUser.getAgency3());
        user.setAgency4(currentUser.getAgency4());
        user.setRole(currentUser.getRole());
        user.setLevel(currentUser.getLevel());
        user.setLoginIp(currentUser.getLoginIp());
        return user;
    }

    public static String userid() {
        CurrentUser user = user();
        return user == null ? null : user.getUserid();
    }

    public static Role role() {
        CurrentUser user = user();
        return user == null ? Role.NONE : user.getRole();
    }

    public static int level() {
        CurrentUser user = user();
        return user == null ? 0 : user.getLevel();
    }

    public static String device() {
        HttpServletRequest request = request();
        if (request == null) {
            return "정보없음";
        }

        Device device = DeviceUtils.getCurrentDevice(request);
        if (device == null) {
            return "정보없음";
        }

        if (device.isMobile()) {
            return "모바일";
        } else if (device.isTablet()) {
            return "태블릿";
        } else if (device.isNormal()) {
            return "컴퓨터";
        } else {
            return "알수없음";
        }
    }

    public static String country(String ip) {
        try (DatabaseReader reader = new DatabaseReader.Builder(new ClassPathResource("GeoLite2-Country.mmdb").getFile()).withCache(new CHMCache()).build()) {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return reader.country(ipAddress).getCountry().getIsoCode();
        } catch (IOException | GeoIp2Exception e) {
            log.info("IP 변환 접속정보 획득 실패 - " + e.getMessage());
            return "IP 변환실패";
        }
    }

    public static String encoding(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("파라미터 인코딩에 실패하였습니다. - {}", e.getMessage());
            return "";
        }
    }

    public static String domain() {
        HttpServletRequest request = request();
        if (request == null) {
            return "알수없음";
        }
        String server = request.getServerName();
        if (server == null) {
            return "알수없음";
        }
        String[] domain = server.split("\\.");
        if (domain.length < 2) {
            return server;
        }
        return domain[domain.length - 2] + "." + domain[domain.length - 1];
    }

    public static String ip() {
        HttpServletRequest request = request();
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (emptyIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (emptyIp(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (emptyIp(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (ip.contains(":")) {
            ip = "unknown";
        }
        return ip;
    }

    public static String logoutMessage(String error) {
        switch (error) {
            case "2":
                return "중복 로그인으로 로그아웃 되었습니다.";
            case "3":
                return "권한이 부족합니다. 다시 로그인 하여 주세요.";
            default:
                return "로그아웃 되었습니다.";
        }
    }

    private static HttpServletRequest request() {
        try {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return sra.getRequest();
        } catch (RuntimeException e) {
            log.info("request 정보를 가져오지 못하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            return null;
        }
    }

    private static boolean emptyIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

}
