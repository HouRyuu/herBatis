package jp.ac.jec.herBatis.parameter;

import jp.ac.jec.entity.UserPO;
import jp.ac.jec.herBatis.io.Resources;
import jp.ac.jec.herBatis.session.SQLSession;
import jp.ac.jec.herBatis.session.SQLSessionFactory;
import jp.ac.jec.herBatis.session.SqlSessionFactoryBuilder;
import jp.ac.jec.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class XmlTest {
    private InputStream in;
    private SQLSession session;
    UserMapper userMapper;

    @BeforeEach
    public void init() {
        // 1.資源ファイルを読み込む
        in = Resources.getResourceAsStream("herBatis/SqlMapConfig.xml");
        // 2.SqlSessionFactory工場を作る
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SQLSessionFactory factory = builder.build(in);
        // 3.工場でSqlSessionを生産する
        session = factory.openSessionWithConfiguration(false);
        userMapper = session.getMapper(UserMapper.class);
    }

    @AfterEach
    public void destroy() throws IOException {
        in.close();
        session.commit();
        session.close();
    }

    @Test
    void getById() {
        // 4.SqlSessionでmapperインタフェースの代理実現を生成する
        assert userMapper.xmlGetById(2).getId() == 2;
    }

    @Test
    void insert() {
        // 4.SqlSessionでmapperインタフェースの代理実現を生成する
        UserPO userPO = new UserPO();
        userPO.setName("雨島1");
        userPO.setBirthday(new Date());
        assert userMapper.xmlInsertUser(userPO) == 1;
    }

    @Test
    void deleteById() {
        assert userMapper.xmlDeleteById(12);
    }

    @Test
    void updateById() {
        UserPO userPO = new UserPO();
        userPO.setId(14);
        userPO.setName("雨島");
        userPO.setBirthday(new Date());
        assert userMapper.xmlUpdateUser(userPO) == 1;
    }
}
