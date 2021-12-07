package spoon.common.net;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import spoon.common.utils.ErrorUtils;

import java.io.*;
import java.net.URL;
import java.util.Map;

@Slf4j
public class HttpParsing {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    public static String getJson(String url) {
        try {
            return Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .maxBodySize(0)
                    .timeout(60 * 1000)
                    .execute()
                    .body();
        } catch (HttpStatusException e) {
            log.warn("에러코드 : {}, 주소 : {}", e.getStatusCode(), url);
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
        } catch (IOException e) {
            log.warn("사이트에 접속할 수 없습니다. - 에러코드: {}, 주소: {}", e.getMessage(), url);
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
        }
        return null;
    }

    public static String postJson(String url) {
        try {
            return Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .maxBodySize(0)
                    .timeout(60 * 1000)
                    .execute()
                    .body();
        } catch (HttpStatusException e) {
            log.warn("에러코드 : {}, 주소 : {}", e.getStatusCode(), url);
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
        } catch (IOException e) {
            log.warn("사이트에 접속할 수 없습니다. - 에러코드: {}, 주소: {}", e.getMessage(), url);
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
        }
        return null;
    }

    public static boolean saveImage(String flagUrl, String filePath) {
        File file = new File(filePath);
        file.deleteOnExit();
        try (InputStream in = new BufferedInputStream(new URL(flagUrl).openStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            int n;
            byte[] buf = new byte[1024];
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            byte[] response = out.toByteArray();
            fos.write(response);

        } catch (IOException e) {
            log.error("이미지를 저장하지 못하였습니다. - flagUrl: {}, filePath: {}, 에러코드: {}", flagUrl, filePath, e.getMessage());
            log.error("{}", ErrorUtils.trace(e.getStackTrace()));
            return false;
        }
        return true;
    }

    public static String postCasinoEvolution(String url, Map<String, String> headers) {
        try {
            return Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .headers(headers)
                    .userAgent(USER_AGENT)
                    .maxBodySize(0)
                    .timeout(60 * 1000)
                    .execute()
                    .body();
        } catch (IOException e) {
            log.error("사이트에 접속할 수 없습니다. - {}, {}", url, headers, e);
        }
        return null;
    }

    public static String getCasinoEvolution(String url, Map<String, String> headers) {
        try {
            return Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .headers(headers)
                    .maxBodySize(0)
                    .timeout(60 * 1000)
                    .execute()
                    .body();
        } catch (IOException e) {
            log.error("사이트에 접속할 수 없습니다. - {}, {}", url, headers, e);
        }
        return null;
    }

    public static String patchCasinoEvolution(String url, Map<String, String> headers) {
        try {
            return Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .userAgent(USER_AGENT)
                    .headers(headers)
                    .maxBodySize(0)
                    .timeout(60 * 1000)
                    .execute()
                    .body();
        } catch (IOException e) {
            log.error("사이트에 접속할 수 없습니다. - {}, {}", url, headers, e);
        }
        return null;
    }
}
