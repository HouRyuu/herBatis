package jp.ac.jec.herBatis.builder;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;
import jp.ac.jec.herBatis.cfg.Foreach;
import jp.ac.jec.herBatis.cfg.IfOGNL;
import jp.ac.jec.herBatis.cfg.Mapper;
import jp.ac.jec.herBatis.excuter.*;
import jp.ac.jec.herBatis.parsing.GenericTokenParser;
import jp.ac.jec.herBatis.parsing.ParameterHandler;
import jp.ac.jec.herBatis.parsing.ParameterMapping;
import jp.ac.jec.herBatis.parsing.ParameterMappingTokenHandler;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;

public class MapperProxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperProxy.class);
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
    public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, OgnlException {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        String key = className + "." + methodName;
        Mapper mapper = mappers.get(key);
        if (null == mapper) {
            throw new RuntimeException("不法Mapper");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("HerBatis SQL: {}", mapper.getQuerySQL());
        }
        Map<String, Object> paraMap = ParameterHandler.handler(method.getParameters(), args);
        StringBuilder sqlSb = new StringBuilder(mapper.getQuerySQL());
        for (IfOGNL ifOGNL : mapper.getIfList()) {
            if (Boolean.TRUE.equals(Ognl.getValue(Ognl.parseExpression(ifOGNL.getTestOgnl()), paraMap))) {
                sqlSb.append(" ").append(ifOGNL.getSql());
            }
            for (Foreach foreach : ifOGNL.getForeachList()) {
                List list = (ArrayList) paraMap.get(foreach.getCollection());
                sqlSb.append(" ").append(foreach.getOpen())
                        .append(StringUtils.join(list, foreach.getSeparator()))
                        .append(foreach.getClose());
            }
        }
        for (Foreach foreach : mapper.getForeachList()) {
            List list = (ArrayList) paraMap.get(foreach.getCollection());
            sqlSb.append(" ").append(foreach.getOpen())
                    .append(StringUtils.join(list, foreach.getSeparator()))
                    .append(foreach.getClose());
        }
        mapper.setQuerySQL(sqlSb.toString());
        // ユーザーが書いたSQLを解析する
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        // プレースホルダーの首尾と代える文字列を指定して、変換しながら、引数を順次に取得する
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        mapper.setQuerySQL(tokenParser.parse(mapper.getQuerySQL()));
        for (ParameterMapping parameterMapping : tokenHandler.getParameterMappings()) {
            mapper.addParam(paraMap.get(parameterMapping.getProperty()));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("JDBC SQL: {}, parameter's length: {}", mapper.getQuerySQL(), mapper.getParams().size());
        }
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
