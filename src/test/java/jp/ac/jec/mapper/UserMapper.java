package jp.ac.jec.mapper;

import jp.ac.jec.entity.UserPO;
import jp.ac.jec.herBatis.annotation.*;

import java.util.List;

public interface UserMapper {

    @Select("SELECT * FROM t_user LIMIT 1")
    UserPO get();

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    UserPO getById(@Param("id") int id);

    @Select("SELECT * FROM t_user")
    List<UserPO> findAll();

    @Insert("INSERT INTO t_user VALUES (6, '中村')")
    int insert();

    @Insert("INSERT INTO t_user VALUES (#{id}, #{name}, #{birthday})")
    int insertUser(UserPO user);

    @Delete("DELETE FROM t_user WHERE id=6")
    int delete();

    @Delete("DELETE FROM t_user WHERE id=#{id}")
    boolean deleteById(@Param("id") int id);

    @Update("UPDATE t_user SET name='田村' WHERE id=6")
    int update();

    @Update("UPDATE t_user SET name=#{name},birthday=#{birthday} WHERE id=#{id}")
    int updateById(UserPO user);

    UserPO xmlGet();

    UserPO xmlGetById(@Param("id") int id);

    List<UserPO> xmlFindAll();

    int xmlInsert();

    int xmlInsertUser(UserPO user);

    boolean xmlDelete();

    boolean xmlDeleteById(@Param("id") int id);

    int xmlUpdate();

    int xmlUpdateUser(UserPO user);


}
