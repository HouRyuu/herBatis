package jp.ac.jec.herBatis.utils;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;
import jp.ac.jec.herBatis.cfg.Configuration;
import jp.ac.jec.herBatis.cfg.Foreach;
import jp.ac.jec.herBatis.cfg.IfOGNL;
import jp.ac.jec.herBatis.cfg.Mapper;
import jp.ac.jec.herBatis.contants.SQLType;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * コンフィグファイルの解析ツール
 */
public class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);


    private static class MainXMLConstant {
        private static final String ENVIRONMENTS = "environments";
        private static final String ENVIRONMENT = "environment";
        private static final String DATA_SOURCE = "dataSource";
        private static final String PROPERTY = "property";
        private static final String NAME = "name";
        private static final String VALUE = "value";
        private static final String DRIVER = "driver";
        private static final String URL = "url";
        private static final String USERNAME = "username";
        private static final String PASSWORD = "password";
        private static final String AUTOCOMMIT = "autoCommit";
    }

    private static class MapperConstant {
        public static final String MAPPERS = "mappers";
        public static final String MAPPER = "mapper";
        public static final String RESOURCE = "resource";
        public static final String NAMESPACE = "namespace";
        public static final String ID = "id";
        public static final String RESULT_TYPE = "resultType";
        public static final String IF = "if";
        public static final String TEST = "test";
        public static final String CLASS = "class";
        public static final String FOREACH = "foreach";
        public static final String COLLECTION = "collection";
        public static final String OPEN = "open";
        public static final String CLOSE = "close";
        public static final String SEPARATOR = "separator";
        public static final String DOT = ".";
    }

    /**
     * メインコンフィグファイルを解析してConfigurationを生じる
     *
     * @param inputStream コンフィグファイル
     * @return Configuration
     * @throws Exception ファイル解析例外
     */
    public static Configuration loadXMLConfiguration(InputStream inputStream) throws Exception {

        Configuration configuration = new Configuration();
        SAXReader reader = new SAXReader();
        Element rootElement = reader.read(inputStream).getRootElement();

        loadMainXML(rootElement, configuration);
        loadMapperXML(rootElement, configuration);


        return configuration;
    }


    /**
     * メインコンフィグファイルを解析する
     *
     * @param rootElement   XMLファイルのルート
     * @param configuration configuration
     */
    private static void loadMainXML(Element rootElement, Configuration configuration) {
        Element dataSourceElement = rootElement.element(MainXMLConstant.ENVIRONMENTS)
                .element(MainXMLConstant.ENVIRONMENT)
                .element(MainXMLConstant.DATA_SOURCE);
        List<Element> properties = dataSourceElement.elements(MainXMLConstant.PROPERTY);
        String attributeName, attributeValue;
        for (Element element : properties) {

            attributeName = element.attributeValue(MainXMLConstant.NAME);
            attributeValue = element.attributeValue(MainXMLConstant.VALUE);
            if (MainXMLConstant.DRIVER.equals(attributeName)) {
                configuration.setDriver(attributeValue);
            } else if (MainXMLConstant.URL.equals(attributeName)) {
                configuration.setUrl(attributeValue);
            } else if (MainXMLConstant.USERNAME.equals(attributeName)) {
                configuration.setUsername(attributeValue);
            } else if (MainXMLConstant.PASSWORD.equals(attributeName)) {
                configuration.setPassword(attributeValue);
            } else if (MainXMLConstant.AUTOCOMMIT.equals(attributeName)) {
                configuration.setAutoCommit(Boolean.parseBoolean(attributeValue));
            }
        }
    }

    /**
     * 解析映射配置文件
     *
     * @param rootElement   XMLファイルのルート
     * @param configuration configuration
     * @throws DocumentException
     * @throws ClassNotFoundException
     */
    private static void loadMapperXML(Element rootElement, Configuration configuration) throws DocumentException, ClassNotFoundException {
        List<Element> mappers = rootElement.element(MapperConstant.MAPPERS).elements(MapperConstant.MAPPER);
        SAXReader reader = new SAXReader();
        for (Element mapperElement : mappers) {

            Map<String, Mapper> mapperMap = new HashMap<>();

            Attribute resourceAttribute = mapperElement.attribute(MapperConstant.RESOURCE);
            // resource属性があればMapperのXMLを解析する。なければアノテーションを解析する
            if (null != resourceAttribute) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("XMLでのMapperファイルを解析し始める");
                }
                String mapperPath = resourceAttribute.getValue();
                InputStream inputStream = XmlUtils.class.getClassLoader().getResourceAsStream(mapperPath);

                Element mapperRootElement = reader.read(inputStream).getRootElement();

                // <namespace>の値を取得
                String namespace = mapperRootElement.attributeValue(MapperConstant.NAMESPACE);
                List<Element> elements = mapperRootElement.elements();
                Mapper mapper;
                for (Element element : elements) {
                    mapper = new Mapper();
                    // MapperXMLの中のSQLのid,resultType,SQLを取得
                    String sqlType = element.getName();
                    String id = element.attributeValue(MapperConstant.ID);
                    String resultType = element.attributeValue(MapperConstant.RESULT_TYPE);

                    String querySql = element.getTextTrim();
                    mapper.setSqlTypeClass(SQLType.getAnnotationClass(sqlType));
                    mapper.setQuerySQL(querySql);
                    mapper.setResultClass(resultType);
                    // ダイナミックSQL
                    List<Element> ifElementList = element.elements(MapperConstant.IF);
                    List<IfOGNL> ifList = ifElementList.stream().map(ifEle -> {
                        IfOGNL ifOGNL = new IfOGNL(ifEle.attributeValue(MapperConstant.TEST), ifEle.getTextTrim());
                        ifOGNL.setForeachList(parseForeach(ifEle));
                        return ifOGNL;
                    }).toList();
                    mapper.setIfList(ifList);
                    mapper.setForeachList(parseForeach(element));
                    // mapperをmapに入れる中。
                    String key = namespace + "." + id;
                    mapperMap.put(key, mapper);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("クラス名でのMapperクラスを解析し始める");
                }
                String classAttributeValue = mapperElement.attributeValue(MapperConstant.CLASS);

                Class<?> aClass = Class.forName(classAttributeValue);
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    // Insert・Delete・Delete・Selectのアノテーションの有無を判断する
                    boolean insertAnnotationPresent = method.isAnnotationPresent(Insert.class),
                            deleteAnnotationPresent = method.isAnnotationPresent(Delete.class),
                            updateAnnotationPresent = method.isAnnotationPresent(Update.class),
                            annotationPresent = method.isAnnotationPresent(Select.class);
                    if (!insertAnnotationPresent && !deleteAnnotationPresent
                            && !updateAnnotationPresent && !annotationPresent) {
                        // throw new RuntimeException("MapperのメソッドにInsert・Delete・Delete・Selectのアノテーションが見つかりませんでした");
                        continue;
                    }
                    Mapper mapper = new Mapper();
                    String querySql = insertAnnotationPresent ? method.getAnnotation(Insert.class).value()
                            : deleteAnnotationPresent ? method.getAnnotation(Delete.class).value()
                            : updateAnnotationPresent ? method.getAnnotation(Update.class).value()
                            : method.getAnnotation(Select.class).value();
                    mapper.setSqlTypeClass(method.getDeclaredAnnotations()[0].annotationType());
                    mapper.setQuerySQL(querySql);
                    mapper.setResultClass(method.getReturnType().getName());
                    // メソッドの戻り値のクラスを取得
                    Type genericReturnType = method.getGenericReturnType();
                    // ジェネリックであれば
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        // ジェネリックの実のクラスを取得（List<User>：User）
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        Class<?> domainClass = (Class<?>) actualTypeArguments[0];
                        mapper.setResultClass(domainClass.getName());
                    }
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();
                    String key = className + MapperConstant.DOT + methodName;
                    mapperMap.put(key, mapper);
                }

            }
            configuration.setMapperMap(mapperMap);
        }
    }

    private static List<Foreach> parseForeach(Element element) {
        return element.elements(MapperConstant.FOREACH).stream().map(foreachEle ->
                new Foreach(foreachEle.attributeValue(MapperConstant.COLLECTION),
                        foreachEle.attributeValue(MapperConstant.OPEN),
                        foreachEle.attributeValue(MapperConstant.CLOSE),
                        foreachEle.attributeValue(MapperConstant.SEPARATOR))).toList();
    }

}
