package com.acme.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MapperHelper {

    public static String getExistString(ResultSet rs, String columnName) throws SQLException {
        String result = null;
        if(isPresent(rs,columnName)) result = rs.getString(columnName);
        return result;
    }

    private static boolean isPresent(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
