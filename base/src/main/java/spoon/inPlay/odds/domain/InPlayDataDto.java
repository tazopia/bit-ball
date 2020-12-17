package spoon.inPlay.odds.domain;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class InPlayDataDto {

    private final String json;

    private InPlayDataDto(byte[] chars) {
        this.json = new String(chars, StandardCharsets.UTF_8);
    }

    public static InPlayDataDto of(byte[] chars) {
        return new InPlayDataDto(chars);
    }
}
