//package com.acme.handlers;
//
//import org.apache.ibatis.type.*;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@MappedJdbcTypes(JdbcType.VARCHAR)
//@MappedTypes(Boolean.class)
//@Alias("yn")
//public class StringBooleanTypeHandler implements TypeHandler<Boolean> {
//
//    private static final String YES = "Y";
//    private static final String NO = "N";
//
//    @Override
//    public void setParameter(PreparedStatement ps, int position, Boolean value,
//                             JdbcType jdbcType) throws SQLException {
//        Boolean bValue = (Boolean) value;
//        ps.setString(position, bValue.booleanValue() ? YES : NO);
//    }
//
//    @Override
//    public Boolean getResult(ResultSet rs, String name) throws SQLException {
//        return valueOf(rs.getString(name));
//    }
//
//    @Override
//    public Boolean getResult(CallableStatement cs, int position)
//            throws SQLException {
//        return valueOf(cs.getString(position));
//    }
//
//    private Boolean valueOf(String value) {
//        return Boolean.valueOf(YES.equalsIgnoreCase(value));
//    }
//
//    @Override
//    public Boolean getResult(ResultSet arg0, int arg1) throws SQLException {
//        return valueOf(arg0.getString(arg1));
//    }
//}
