package spoon.gameZone.bitcoin1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.gameZone.bitcoin1.entity.Bitcoin1;

@Data
public class Bitcoin1Score {

    @JsonIgnore
    private Bitcoin1 bitcoin;

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

    private Bitcoin1Score(Bitcoin1 bitcoin) {
        this.bitcoin = bitcoin;
    }

    public static Bitcoin1Score of(Bitcoin1 bitcoin1) {
        return new Bitcoin1Score(bitcoin1);
    }
}
