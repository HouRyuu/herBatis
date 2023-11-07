package jp.ac.jec.mapper;

import jp.ac.jec.entity.UserPO;
import jp.ac.jec.herBatis.annotation.Param;
import jp.ac.jec.herBatis.annotation.Select;

public interface UserMapper {

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    UserPO get(@Param("id") int id);

}
