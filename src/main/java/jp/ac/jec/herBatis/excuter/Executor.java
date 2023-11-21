package jp.ac.jec.herBatis.excuter;

import jp.ac.jec.herBatis.cfg.Mapper;

import java.sql.Connection;

public interface Executor {

    Object execute(Mapper mapper, Connection conn);

}
