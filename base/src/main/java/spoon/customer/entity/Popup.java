package spoon.customer.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "POPUP")
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    @Column(length = 1024)
    private String summary;

    @Column(length = 32)
    private String sdate;

    private boolean enabled;

    private int sort;

}
