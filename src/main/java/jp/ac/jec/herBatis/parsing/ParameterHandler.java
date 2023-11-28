package jp.ac.jec.herBatis.parsing;

import jp.ac.jec.herBatis.annotation.Param;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParameterHandler {

    public static Map<String, Object> handler(Parameter[] parameters, Object[] args) {
        if (ArrayUtils.isEmpty(parameters)) {
            return Collections.emptyMap();
        }
        Map<String, Object> paramMap = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Param annotation = parameters[i].getAnnotation(Param.class);
            if (annotation == null) continue;
            paramMap.put(annotation.value(), args[i]);
        }
        return paramMap;
    }

}
