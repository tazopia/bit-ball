package spoon.gameZone.bitcoin5.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.gameZone.bitcoin5.entity.Bitcoin5;

@Data
public class Bitcoin5Score {

    @JsonIgnore
    private Bitcoin5 bitcoin;

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

    private Bitcoin5Score(Bitcoin5 bitcoin) {
        this.bitcoin = bitcoin;
    }

    public static Bitcoin5Score of(Bitcoin5 bitcoin5) {
        return new Bitcoin5Score(bitcoin5);
    }
}
