package spoon.support.convert;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class LongArrayConvert implements AttributeConverter<long[], String> {

    @Override
    public String convertToDatabaseColumn(long[] attribute) {
        return attribute == null ? null : Arrays.stream(attribute).mapToObj(Long::toString).collect(joining(","));
    }

    @Override
    public long[] convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Arrays.stream(dbData.split(",")).mapToLong(Long::parseLong).toArray();
    }
}
