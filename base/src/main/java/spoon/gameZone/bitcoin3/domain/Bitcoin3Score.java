package spoon.gameZone.bitcoin3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.gameZone.bitcoin3.entity.Bitcoin3;

@Data
public class Bitcoin3Score {

    @JsonIgnore
    private Bitcoin3 bitcoin;

    public String getDate() {
        return DateUtils.format(bitcoin.getGameDate(), "MM-dd (E)");
    }

    public String getTime() {
        return DateUtils.format(bitcoin.getGameDate(), "HH:mm");
    }

    public double getOpen() {
        return bitcoin.getOpen();
    }

    public double getClose() {
        return bitcoin.getClose();
    }

    public double getHigh() {
        return bitcoin.getHigh();
    }

    public double getLow() {
        return bitcoin.getLow();
    }

    public String getBs() {
        return bitcoin.getBs();
    }

    public String getHiOe() {
        return bitcoin.getHi_oe();
    }

    public String getHiOu() {
        return bitcoin.getHi_ou();
    }

    public String getLoOe() {
        return bitcoin.getLo_oe();
    }

    public String getLoOu() {
        return bitcoin.getLo_ou();
    }

    private Bitcoin3Score(Bitcoin3 bitcoin) {
        this.bitcoin = bitcoin;
    }

    public static Bitcoin3Score of(Bitcoin3 bitcoin3) {
        return new Bitcoin3Score(bitcoin3);
    }
}
