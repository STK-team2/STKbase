package team2.stk.shared.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.util.Map;

/**
 * JSONB 컬럼을 Map&lt;String, Object&gt;로 변환하는 JPA AttributeConverter
 */
@Converter
public class JsonbConverter implements AttributeConverter<Map<String, Object>, Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(objectMapper.writeValueAsString(attribute));
            return pgObject;
        } catch (Exception e) {
            throw new IllegalArgumentException("JSONB 변환 실패: Map -> jsonb", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            String json = dbData instanceof PGobject
                    ? ((PGobject) dbData).getValue()
                    : dbData.toString();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("JSONB 변환 실패: jsonb -> Map", e);
        }
    }
}
