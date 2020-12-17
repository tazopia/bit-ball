package spoon.sun.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@ToString
@Getter
@Entity
@Table(name = "SUN_ID")
public class SunId {

    @Id
    @Column(length = 64, nullable = false, unique = true)
    private String userid;

    @Column(length = 64, nullable = false, unique = true)
    private String gnum;

    private SunId(String userid, String gnum) {
        this.userid = userid;
        this.gnum = gnum;
    }

    public static SunId of(String userid, String gnum) {
        return new SunId(userid, gnum);
    }
}
