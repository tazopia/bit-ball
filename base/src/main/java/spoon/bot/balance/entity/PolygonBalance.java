package spoon.bot.balance.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "POLYGON_BALANCE")
public class PolygonBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String game;

    private String gameDate;

    private String round;

    private String gameType;

    private String betType;

    private long price;

    private String message;

    private Date regDate;

}
