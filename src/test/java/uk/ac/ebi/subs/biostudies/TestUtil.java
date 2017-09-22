package uk.ac.ebi.subs.biostudies;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

public class TestUtil {
    public static Object loadObjectFromJson(String filePath, Class<?> clazz) {
        Object result;
        try {
            String json = IOUtils.toString(
                    TestUtil.class.getClassLoader().getResourceAsStream(filePath),
                    "UTF-8"
            );
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            result = objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
