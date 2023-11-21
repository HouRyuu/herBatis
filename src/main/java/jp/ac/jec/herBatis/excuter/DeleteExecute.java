package jp.ac.jec.herBatis.excuter;

import jp.ac.jec.herBatis.cfg.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteExecute implements Executor {
    @Override
    public Object execute(Mapper mapper, Connection conn) {
        String querySql = mapper.getQuerySQL();
        try (PreparedStatement preparedStatement = conn.prepareStatement(querySql)) {
            int delCount = preparedStatement.executeUpdate();
            System.out.println(delCount);
            return delCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
