package jp.ac.jec.herBatis.excuter;

import jp.ac.jec.herBatis.cfg.Mapper;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class SelectExecutor implements Executor {
    @Override
    public Object execute(Mapper mapper, Connection conn) {
        String querySql = mapper.getQuerySQL();
        String resultClassName = mapper.getResultClass();
        ResultSet rs = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(querySql)) {
            for (int i = 0; i < mapper.getParams().size(); i++) {
                preparedStatement.setObject(i + 1, mapper.getParams().get(i));
            }
            Class<?> domainClass = Class.forName(resultClassName);
            rs = preparedStatement.executeQuery();

            List<Object> result = new ArrayList<>();
            while (rs.next()) {
                Object obj = domainClass.newInstance();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(columnName);
                    PropertyDescriptor pd = new PropertyDescriptor(columnName, obj.getClass());
                    Method writeMethod = pd.getWriteMethod();
                    writeMethod.invoke(obj, columnValue);
                }
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release(rs);
        }

    }

    private void release(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // if (pstm != null) {
        //     try {
        //         pstm.close();
        //     } catch (SQLException e) {
        //         e.printStackTrace();
        //     }
        // }
    }
}
