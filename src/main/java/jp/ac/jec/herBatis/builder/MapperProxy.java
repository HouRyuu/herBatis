package jp.ac.jec.herBatis.builder;

import jp.ac.jec.herBatis.annotation.*;
import jp.ac.jec.herBatis.excuter.Executer;
import jp.ac.jec.herBatis.excuter.SelectExecuter;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapperProxy implements InvocationHandler {
    private static final Map<Class<?>, Executer<?>> SQL_EXECUTER_MAP = new HashMap<>();

    static {
        SQL_EXECUTER_MAP.put(Insert.class, new SelectExecuter());
        SQL_EXECUTER_MAP.put(Delete.class, new SelectExecuter());
        SQL_EXECUTER_MAP.put(Update.class, new SelectExecuter());
        SQL_EXECUTER_MAP.put(Select.class, new SelectExecuter());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var annotations = method.getAnnotations();
        // XMLで書いたSQL
        if (ArrayUtils.isEmpty(annotations)) {
            return method.invoke(proxy, args);
        }
        Arrays.stream(method.getParameters()).forEach((p) -> {
            var annotation = p.getAnnotation(Param.class);
            var value = annotation.value();
        });
        // アノテーション型のSQL
        SQL_EXECUTER_MAP.get(annotations[0].getClass()).execute(null, null);
        return null;
    }
}
