package jp.ac.jec.herBatis.contants;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;

import java.util.HashMap;
import java.util.Map;

public final class SQLType {

    public static final String INSERT = "insert";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String SELECT = "select";

    private static final Map<String, Class<?>> annotationMap = new HashMap<>();

    static {
        annotationMap.put(INSERT, Insert.class);
        annotationMap.put(DELETE, Delete.class);
        annotationMap.put(UPDATE, Update.class);
        annotationMap.put(SELECT, Select.class);
    }

    public static Class<?> getAnnotationClass(String sqlType) {
        return annotationMap.get(sqlType);
    }

}
