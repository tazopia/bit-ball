package spoon.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    NONE("에러", Group.USER),
    DUMMY("NPC", Group.USER),
    USER("회원", Group.USER),
    AGENCY1("매장", Group.AGENCY),
    AGENCY2("총판", Group.AGENCY),
    AGENCY3("부본사", Group.AGENCY),
    AGENCY4("총본사", Group.AGENCY),
    ADMIN("관리자", Group.ADMIN),
    SUPER("슈퍼관리자", Group.ADMIN),
    GOD("시스템운영자", Group.ADMIN);

    private final String name;
    private final Group group;

    public enum Group {
        USER, AGENCY, ADMIN
    }

}
