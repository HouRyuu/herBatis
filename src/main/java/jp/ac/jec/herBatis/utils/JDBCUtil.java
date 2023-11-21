package jp.ac.jec.herBatis.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class JDBCUtil {

    public static Connection connection() {
        return null;
    }

    public static <T> T selectOne(String sql, Object[] params) {
        return null;
    }

    public static <T> T select(String sql, Object[] params) throws SQLException {
        Connection conn = connection();
        PreparedStatement preState = conn.prepareStatement(sql);
        bindParam(preState, params);
        ResultSet rSet = preState.executeQuery();
        T result = null;
        T a = (T) convertSet(rSet, result.getClass());
        conn.close();
        return result;
    }

    public static int execute(String sql, Object[] params) {
        return 0;
    }

    private static void bindParam(PreparedStatement preState, Object[] params) throws SQLException {
        for (int i = 0; i < params.length && params[i] != null; i++) {
            preState.setObject(i + 1, params[i]);
            //			if (params[i] instanceof Boolean) {
            //				preState.setBoolean(i + 1, (boolean) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Byte) {
            //				preState.setByte(i + 1, (byte) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Short) {
            //				preState.setShort(i + 1, (short) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Integer) {
            //				preState.setInt(i + 1, (int) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Long) {
            //				preState.setLong(i + 1, (long) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Float) {
            //				preState.setFloat(i + 1, (float) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Double) {
            //				preState.setDouble(i + 1, (double) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof BigDecimal) {
            //				preState.setBigDecimal(i + 1, (BigDecimal) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof String) {
            //				preState.setString(i + 1, params[i].toString());
            //				continue;
            //			}
            //			if (params[i] instanceof Blob) {
            //				preState.setBlob(i + 1, (Blob) params[i]);
            //				continue;
            //			}
            //			if (params[i] instanceof Clob) {
            //				preState.setClob(i + 1, (Clob) params[i]);
            //				continue;
            //			}
        }
    }

    /**
     * ResultSetを指定された型に変える
     *
     * @param rSet ResultSet
     * @return 指定された型のデータ
     */
    private static Object convertSet(ResultSet rSet, Class<?> targetClass)
            throws SQLException {
        if (targetClass == Set.class) {
            if (!rSet.next()) {
                return Collections.EMPTY_SET;
            }
        }
        if (targetClass == List.class) {
            if (!rSet.next()) {
                return Collections.EMPTY_LIST;
            }
        }

        return null;
    }

}
