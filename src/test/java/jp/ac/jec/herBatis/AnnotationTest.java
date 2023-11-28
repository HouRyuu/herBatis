package jp.ac.jec.herBatis;

import jp.ac.jec.entity.UserPO;
import jp.ac.jec.herBatis.io.Resources;
import jp.ac.jec.herBatis.session.SQLSession;
import jp.ac.jec.herBatis.session.SQLSessionFactory;
import jp.ac.jec.herBatis.session.SqlSessionFactoryBuilder;
import jp.ac.jec.mapper.UserMapper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AnnotationTest {
    private InputStream in;
    private SQLSession session;

    @BeforeEach
    public void init() {
        // 1.資源ファイルを読み込む
        in = Resources.getResourceAsStream("herBatis/SqlMapConfig.xml");
        // 2.SqlSessionFactory工場を作る
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SQLSessionFactory factory = builder.build(in);
        // 3.工場でSqlSessionを生産する
        session = factory.openSessionWithConfiguration();
    }

    @AfterEach
    public void destroy() throws IOException {
        in.close();
        session.commit();
        session.close();
    }

    @Test
    void select() {
        // 4.SqlSessionでmapperインタフェースの代理実現を生成する
        UserMapper userMapper = session.getMapper(UserMapper.class);
        assert userMapper.get().getId() == 1;
        // 5.代理mapperでメソッドを実行する
        List<UserPO> users = userMapper.findAll();
        assert users.size() == 5;
        for (UserPO user : users) {
            System.out.println(user);
        }
    }

    @Test
    void getById() {
        // 4.SqlSessionでmapperインタフェースの代理実現を生成する
        UserMapper userMapper = session.getMapper(UserMapper.class);
        assert userMapper.getById(2).getId() == 2;
    }

    @Test
    void insert() {
        UserMapper userMapper = session.getMapper(UserMapper.class);
        assert userMapper.insert() == 1;
    }

    @Test
    void update() {
        UserMapper userMapper = session.getMapper(UserMapper.class);
        assert userMapper.update() == 1;
    }

    @Test
    void delete() {
        UserMapper userMapper = session.getMapper(UserMapper.class);
        assert userMapper.delete() == 1;
    }


}
