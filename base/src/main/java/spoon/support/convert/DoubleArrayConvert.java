package spoon.support.convert;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class DoubleArrayConvert implements AttributeConverter<double[], String> {

    @Override
    public String convertToDatabaseColumn(double[] attribute) {
        return attribute == null ? null : Arrays.stream(attribute).mapToObj(Double::toString).collect(joining(","));
    }

    @Override
    public double[] convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Arrays.stream(dbData.split(",")).mapToDouble(Double::parseDouble).toArray();
    }
}
