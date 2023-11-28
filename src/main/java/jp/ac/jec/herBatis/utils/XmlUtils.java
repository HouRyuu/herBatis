package jp.ac.jec.herBatis.utils;

import jp.ac.jec.herBatis.annotation.Delete;
import jp.ac.jec.herBatis.annotation.Insert;
import jp.ac.jec.herBatis.annotation.Select;
import jp.ac.jec.herBatis.annotation.Update;
import jp.ac.jec.herBatis.cfg.Configuration;
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
        Element dataSourceElement = rootElement.element("environments").element("environment").element("dataSource");
        List<Element> properties = dataSourceElement.elements("property");
        String attributeName, attributeValue;
        for (Element element : properties) {

            attributeName = element.attributeValue("name");
            attributeValue = element.attributeValue("value");
            if ("driver".equals(attributeName)) {
                configuration.setDriver(attributeValue);
            } else if ("url".equals(attributeName)) {
                configuration.setUrl(attributeValue);
            } else if ("username".equals(attributeName)) {
                configuration.setUsername(attributeValue);
            } else if ("password".equals(attributeName)) {
                configuration.setPassword(attributeValue);
            } else if ("autoCommit".equals(attributeName)) {
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
        List<Element> mappers = rootElement.element("mappers").elements("mapper");
        SAXReader reader = new SAXReader();
        for (Element mapperElement : mappers) {

            Map<String, Mapper> mapperMap = new HashMap<>();

            Attribute resourceAttribute = mapperElement.attribute("resource");
            // resource属性があればMapperのXMLを解析する。なければアノテーションを解析する
            if (null != resourceAttribute) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("XMLでのMapperファイルを解析し始める");
                }
                String mapperPath = resourceAttribute.getValue();
                InputStream inputStream = XmlUtils.class.getClassLoader().getResourceAsStream(mapperPath);

                Element mapperRootElement = reader.read(inputStream).getRootElement();

                // <namespace>の値を取得
                String namespace = mapperRootElement.attributeValue("namespace");
                List<Element> elements = mapperRootElement.elements();
                Mapper mapper;
                for (Element element : elements) {
                    mapper = new Mapper();
                    // MapperXMLの中のSQLのid,resultType,SQLを取得
                    String sqlType = element.getName();
                    String id = element.attributeValue("id");
                    String resultType = element.attributeValue("resultType");
                    String querySql = element.getStringValue();
                    mapper.setSqlTypeClass(SQLType.getAnnotationClass(sqlType));
                    mapper.setQuerySQL(querySql);
                    mapper.setResultClass(resultType);
                    // mapperをmapに入れる中。
                    String key = namespace + "." + id;
                    mapperMap.put(key, mapper);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("クラス名でのMapperクラスを解析し始める");
                }
                String classAttributeValue = mapperElement.attributeValue("class");

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
                    if (genericReturnType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                        // ジェネリックの実のクラスを取得（List<User>：User）
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        Class<?> domainClass = (Class<?>) actualTypeArguments[0];
                        mapper.setResultClass(domainClass.getName());
                    }
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();
                    String key = className + "." + methodName;
                    mapperMap.put(key, mapper);
                }

            }
            configuration.setMapperMap(mapperMap);
        }
    }
}
