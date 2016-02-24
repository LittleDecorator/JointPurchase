//package com.acme.handlers;
//
//import org.apache.ibatis.type.Alias;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.MappedJdbcTypes;
//
//import java.io.IOException;
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@MappedJdbcTypes(JdbcType.VARCHAR)
//@Alias("base64")
//public class Base64BytesTypeHandler extends BaseTypeHandler<byte[]> {
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, byte[] parameter, JdbcType jdbcType) throws SQLException {
//        try {
//            ps.setString(i, Base64BytesSerializer.serialize(parameter));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public byte[] getNullableResult(ResultSet rs, String columnName)
//            throws SQLException {
//        try {
//            return Base64BytesSerializer.deserialize(rs.getString(columnName));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public byte[] getNullableResult(ResultSet rs, int columnIndex)
//            throws SQLException {
//        try {
//            return Base64BytesSerializer.deserialize(rs.getString(columnIndex));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public byte[] getNullableResult(CallableStatement cs, int columnIndex)
//            throws SQLException {
//        try {
//            return Base64BytesSerializer.deserialize(cs.getString(columnIndex));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
