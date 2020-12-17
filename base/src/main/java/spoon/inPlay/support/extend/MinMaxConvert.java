package spoon.inPlay.support.extend;

import spoon.common.utils.JsonUtils;
import spoon.inPlay.config.domain.InPlayMinMax;

import javax.persistence.AttributeConverter;

public class MinMaxConvert implements AttributeConverter<InPlayMinMax, String> {

    @Override
    public String convertToDatabaseColumn(InPlayMinMax attribute) {
        return JsonUtils.toString(attribute);
    }

    @Override
    public InPlayMinMax convertToEntityAttribute(String dbData) {
        return dbData == null ? new InPlayMinMax() : JsonUtils.toModel(dbData, InPlayMinMax.class);
    }
}
