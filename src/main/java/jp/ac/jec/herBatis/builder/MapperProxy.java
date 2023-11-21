package jp.ac.jec.herBatis.builder;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;
import jp.ac.jec.herBatis.cfg.Mapper;
import jp.ac.jec.herBatis.excuter.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

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
            throw new IllegalArgumentException("不法Mapper");
        }
        return SQL_EXECUTER_MAP.get(mapper.getSqlTypeClass()).execute(mapper, connection);
    }
}
