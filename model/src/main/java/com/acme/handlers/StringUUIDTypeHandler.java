package com.acme.handlers;

import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(String.class)
@Alias("uuidTH")
public class StringUUIDTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    private String uuid2String(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return uuid2String(UUID.class.cast(rs.getObject(columnName)));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return uuid2String(UUID.class.cast(rs.getObject(columnIndex)));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return uuid2String(UUID.class.cast(cs.getObject(columnIndex)));
    }

}
