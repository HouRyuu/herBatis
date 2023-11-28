package jp.ac.jec.herBatis.builder;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;
import jp.ac.jec.herBatis.cfg.Mapper;
import jp.ac.jec.herBatis.excuter.*;
import jp.ac.jec.herBatis.parsing.GenericTokenParser;
import jp.ac.jec.herBatis.parsing.ParameterHandler;
import jp.ac.jec.herBatis.parsing.ParameterMapping;
import jp.ac.jec.herBatis.parsing.ParameterMappingTokenHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;

public class MapperProxy implements InvocationHandler {
    private static final Map<Class<?>, Executor> SQL_EXECUTER_MAP = new HashMap<>();

    private final Map<String, Mapper> mappers;
    private final Connection connection;

    static {
        SQL_EXECUTER_MAP.put(Insert.class, new InsertExecute());
        SQL_EXECUTER_MAP.put(Delete.class, new DeleteExecute());
        SQL_EXECUTER_MAP.put(Update.class, new UpdateExecute());
        SQL_EXECUTER_MAP.put(Select.class, new SelectExecutor());
    }

    public MapperProxy(Map<String, Mapper> mappers, Connection connection) {
        this.mappers = mappers;
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        String key = className + "." + methodName;
        Mapper mapper = mappers.get(key);
        if (null == mapper) {
            throw new RuntimeException("不法Mapper");
        }
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        mapper.setQuerySQL(tokenParser.parse(mapper.getQuerySQL()));
        Map<String, Object> paraMap = ParameterHandler.handler(method.getParameters(), args);
        for (ParameterMapping parameterMapping : tokenHandler.getParameterMappings()) {
            mapper.addParam(paraMap.get(parameterMapping.getProperty()));
        }
        List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();
        Object result = SQL_EXECUTER_MAP.get(mapper.getSqlTypeClass()).execute(mapper, connection);
        if (mapper.getSqlTypeClass() == Select.class) {
            if (method.getGenericReturnType() instanceof ParameterizedType) {
                return result;
            }
            List<Object> resultList = (ArrayList<Object>) result;
            if (resultList.isEmpty()) {
                return null;
            }
            if (resultList.size() > 1) {
                throw new RuntimeException("結果は1つ超えました");
            }
            return resultList.get(0);
        }
        if (method.getReturnType() == boolean.class
                || method.getReturnType() == Boolean.class) {
            return (int) result > 0;
        }
        return result;
    }
}
