package jp.ac.jec.herBatis.parsing;

import jp.ac.jec.herBatis.annotation.Param;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterHandler {

    /**
     * 引数をmapに変換する
     *
     * @param parameters 引数基本情報（タイプ、アノテーションなど）
     * @param args       引数の値
     * @return map.key 引数名 map.value 値
     * @throws IllegalAccessException リフレクションでインスタンスの属性の値を取得する例外
     */
    public static Map<String, Object> handler(Parameter[] parameters, Object[] args) throws IllegalAccessException {
        if (ArrayUtils.isEmpty(parameters)) {
            return Collections.emptyMap();
        }
        Map<String, Object> paramMap = new HashMap<>();
        Class<?> paramType;
        for (int i = 0; i < parameters.length; i++) {
            Param annotation = parameters[i].getAnnotation(Param.class);
            paramType = parameters[i].getType();
            // 基本データ型
            if (annotation != null && (paramType.getPackageName().startsWith("java.lang") || paramType == List.class)) {
                paramMap.put(annotation.value(), args[i]);
            }
            // Map
            if (Map.class.isAssignableFrom(parameters[i].getType())) {
                paramMap.putAll((Map) args[i]);
            }
            // カスタマイズデータ型
            Field[] fields = paramType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                paramMap.put(field.getName(), field.get(args[i]));
            }
        }
        return paramMap;
    }

}
