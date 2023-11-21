package jp.ac.jec.herBatis.session;

import jp.ac.jec.herBatis.cfg.Configuration;
import jp.ac.jec.herBatis.utils.XmlUtils;

import java.io.InputStream;

/**
 * SqlSession工場を作る
 */
public class SqlSessionFactoryBuilder {
    public SQLSessionFactory build(InputStream inputStream) {
        Configuration configuration = null;
        try {
            configuration = XmlUtils.loadXMLConfiguration(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("コンフィグファイルに異常あり", e);
        }

        return new DefaultSQLSessionFactory(configuration);
    }
}
