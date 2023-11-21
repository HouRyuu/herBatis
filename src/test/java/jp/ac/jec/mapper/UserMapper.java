package jp.ac.jec.mapper;

import jp.ac.jec.entity.UserPO;
import jp.ac.jec.herBatis.annotation.*;

import java.util.List;

public interface UserMapper {

    @Select("SELECT * FROM t_user LIMIT 1")
    UserPO get();

    @Select("SELECT * FROM t_user")
    List<UserPO> findAll();

    @Insert("INSERT INTO t_user VALUES (6, '中村')")
    int insert();

    @Delete("DELETE FROM t_user WHERE id=6")
    int delete();

    @Update("UPDATE t_user SET name='田村' WHERE id=6")
    int update();
}
