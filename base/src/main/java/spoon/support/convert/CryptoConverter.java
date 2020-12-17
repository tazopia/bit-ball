package spoon.support.convert;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import spoon.common.utils.ErrorUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.io.UnsupportedEncodingException;
import java.security.Key;

@Slf4j
@Convert
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String KEY = "tmvnstkdlxmeoqkrskwkRkRjd!^&"; // 16자리 이상
    private String ips;
    private Key keySpec;

    public CryptoConverter() {
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = KEY.getBytes("UTF-8");
            System.arraycopy(b, 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            this.ips = KEY.substring(0, 16);
            this.keySpec = keySpec;
        } catch (UnsupportedEncodingException e) {
            throw new ConvertException("key로 변환할 수 없습니다.");
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes()));
            byte[] encrypted = cipher.doFinal(attribute.getBytes("UTF-8"));

            return new String(Base64.encodeBase64(encrypted));
        } catch (Exception e) {
            throw new ConvertException("암호화에 실패하였습니다.");
        }
    }

    @Override
    public String convertToEntityAttribute(final String dbData) {
        if (dbData == null) return "";

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes()));
            byte[] byteStr = Base64.decodeBase64(dbData.getBytes());

            return new String(cipher.doFinal(byteStr), "UTF-8");
        } catch (Exception e) {
            log.error("복호화에 실패하였습니다.");
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            return "해킹당했음";
        }
    }
}
